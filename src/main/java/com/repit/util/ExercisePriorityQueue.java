package com.repit.util;

import com.repit.Model.Exercise;
import com.repit.Model.TargetedMuscle;
import com.repit.Model.enums.MuscleGroup;
import com.repit.Model.enums.WorkoutType;

import java.util.*;

/**
 * ExercisePriorityQueue
 * Pure utility — no database access.
 * Ranks and orders exercises for a given workout day based on how well they
 * match the day's target muscles and how much time the user has available.
 *
 * Scoring per exercise:
 * - Primary muscle match (exercise.getMuscle()) -> +2 points
 * - Secondary muscle match (exercise.getSecondaryMuscles()) -> +1 point per match
 * Exercises with a score of 0 are excluded (wrong day entirely).
 *
 * Time budget — each exercise costs 10 minutes (2 warmup + 2 working sets + rest):
 * - 30 min -> 3 exercises: 2 compounds + 1 isolation
 * - 40 min -> 4 exercises: 2 compounds + 2 isolations
 * - 50 min -> 5 exercises: 3 compounds + 2 isolations
 * - 60 min -> 6 exercises: 4 compounds + 2 isolations
 *
 * Isolation cap is always 2 regardless of time — extra time goes to more compounds.
 * Isolation slots are filled by category priority so push day never ends up with
 * 2 tricep isolations and 0 shoulder isolations.
 *
 * Push isolation priority -> [TRICEPS, SHOULDERS]
 * Pull isolation priority -> [BICEPS, BICEPS]
 * Legs isolation priority -> [QUADS, HAMSTRINGS]
 */
public class ExercisePriorityQueue {

    // each exercise = 10 minutes: 2 warmup sets + 2 working sets + rest between sets
    private static final int MINUTES_PER_EXERCISE = 10;

    // points awarded for muscle group matches
    private static final int PRIMARY_SCORE = 2;
    private static final int SECONDARY_SCORE = 1;

    // 2 isolations max — extra time is better spent on more compound work
    private static final int MAX_ISOLATION = 2;

    // minimum compounds we always want regardless of time
    private static final int MIN_COMPOUND = 2;

    /*
     * Maps each WorkoutType to the MuscleGroup values it targets.
     * Used to score each exercise against the day's workout type.
     */
    private static final Map<WorkoutType, Set<MuscleGroup>> WORKOUT_CATEGORY_MAP = new HashMap<>();

    /*
     * Maps each WorkoutType to an ordered list of isolation slot priorities.
     * One slot is filled per category in order, preventing duplicate isolations.
     * Example: PUSH -> [TRICEPS, SHOULDERS] fills the best tricep first, then best shoulder.
     */
    private static final Map<WorkoutType, List<MuscleGroup>> ISOLATION_PRIORITY_MAP = new HashMap<>();

    static {
        // push day: chest, shoulders, triceps
        Set<MuscleGroup> pushMuscles = new HashSet<>(Arrays.asList(
                MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS));

        // pull day: back and biceps
        Set<MuscleGroup> pullMuscles = new HashSet<>(Arrays.asList(
                MuscleGroup.BACK, MuscleGroup.BICEPS));

        // leg day: quads, hamstrings, glutes, calves
        Set<MuscleGroup> legMuscles = new HashSet<>(Arrays.asList(
                MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS,
                MuscleGroup.GLUTES, MuscleGroup.CALVES));

        // upper = push + pull combined
        Set<MuscleGroup> upperMuscles = new HashSet<>();
        upperMuscles.addAll(pushMuscles);
        upperMuscles.addAll(pullMuscles);

        // lower = same as legs
        Set<MuscleGroup> lowerMuscles = new HashSet<>(legMuscles);

        // full body = everything
        Set<MuscleGroup> fullBodyMuscles = new HashSet<>(upperMuscles);
        fullBodyMuscles.addAll(lowerMuscles);

        WORKOUT_CATEGORY_MAP.put(WorkoutType.PUSH, pushMuscles);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.PULL, pullMuscles);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.LEGS, legMuscles);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.UPPER, upperMuscles);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.LOWER, lowerMuscles);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.FULL_BODY, fullBodyMuscles);

        // non-strength days have no muscle targets — no exercises selected by this queue
        WORKOUT_CATEGORY_MAP.put(WorkoutType.CARDIO, new HashSet<>());
        WORKOUT_CATEGORY_MAP.put(WorkoutType.REST, new HashSet<>());
        WORKOUT_CATEGORY_MAP.put(WorkoutType.CORE, new HashSet<>());

        // isolation priority — which muscle group gets slot 1, which gets slot 2
        // PUSH: tricep first then shoulder
        // triceps are the smaller muscle and benefit most from dedicated isolation after pressing
        ISOLATION_PRIORITY_MAP.put(WorkoutType.PUSH,
                Arrays.asList(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS));

        // PULL: both slots go to biceps
        // back is already covered by compound rows/pulldowns
        ISOLATION_PRIORITY_MAP.put(WorkoutType.PULL,
                Arrays.asList(MuscleGroup.BICEPS, MuscleGroup.BICEPS));

        // LEGS: quad first then hamstring
        ISOLATION_PRIORITY_MAP.put(WorkoutType.LEGS,
                Arrays.asList(MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS));

        // UPPER: tricep then bicep
        ISOLATION_PRIORITY_MAP.put(WorkoutType.UPPER,
                Arrays.asList(MuscleGroup.TRICEPS, MuscleGroup.BICEPS));

        // LOWER: quad then hamstring
        ISOLATION_PRIORITY_MAP.put(WorkoutType.LOWER,
                Arrays.asList(MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS));

        // FULL_BODY: tricep then bicep
        ISOLATION_PRIORITY_MAP.put(WorkoutType.FULL_BODY,
                Arrays.asList(MuscleGroup.TRICEPS, MuscleGroup.BICEPS));
    }

    // set in buildQueue() so scoreExercise() knows which muscles to match against
    private WorkoutType currentWorkoutType;

    // compounds always go first — a separate heap prevents a high-scoring isolation
    // from jumping ahead of a lower-scoring compound, which we never want
    private final PriorityQueue<Exercise> compoundHeap;

    // one heap per MuscleGroup so we can pick the best tricep and best shoulder
    // isolation independently instead of having them compete against each other
    private final Map<MuscleGroup, PriorityQueue<Exercise>> isolationHeaps;

    public ExercisePriorityQueue() {
        this.compoundHeap = new PriorityQueue<>((a, b) -> scoreExercise(b) - scoreExercise(a));
        this.isolationHeaps = new HashMap<>();
        for (MuscleGroup mg : MuscleGroup.values()) {
            isolationHeaps.put(mg, new PriorityQueue<>((a, b) -> scoreExercise(b) - scoreExercise(a)));
        }
    }

    /*
     * buildQueue
     * Loads exercises into heaps, scores each one against the workout type,
     * then returns an ordered list that respects the time limit and compound/isolation split.
     *
     * @param exercises all exercises available to the user
     * @param workoutType the type of workout day (PUSH, PULL, LEGS, etc.)
     * @param availableMinutes total minutes available — must be a positive multiple of 10
     * @return ordered list: compounds first, then isolations in priority order
     * @throws IllegalArgumentException if inputs are invalid
     */
    public List<Exercise> buildQueue(List<Exercise> exercises,
                                     WorkoutType workoutType,
                                     int availableMinutes) {
        if (exercises == null || exercises.isEmpty()) {
            throw new IllegalArgumentException("exercises cannot be null or empty.");
        }
        if (workoutType == null) {
            throw new IllegalArgumentException("workoutType cannot be null.");
        }
        if (availableMinutes <= 0 || availableMinutes % MINUTES_PER_EXERCISE != 0) {
            throw new IllegalArgumentException(
                    "availableMinutes must be a positive multiple of "
                            + MINUTES_PER_EXERCISE + ", got: " + availableMinutes);
        }

        this.currentWorkoutType = workoutType;

        // clear heaps from any previous call
        compoundHeap.clear();
        for (PriorityQueue<Exercise> heap : isolationHeaps.values()) heap.clear();

        // load each exercise into the right heap — skip score-0 exercises (wrong day)
        for (Exercise exercise : exercises) {
            if (scoreExercise(exercise) > 0) {
                if (exercise.isCompound()) {
                    compoundHeap.offer(exercise);
                } else {
                    PriorityQueue<Exercise> heap = isolationHeaps.get(exercise.getMuscle());
                    if (heap != null) heap.offer(exercise);
                }
            }
        }

        int totalSlots = availableMinutes / MINUTES_PER_EXERCISE;
        int isolationSlots = totalSlots > MIN_COMPOUND
                ? Math.min(MAX_ISOLATION, totalSlots - MIN_COMPOUND) : 0;
        int compoundSlots = totalSlots - isolationSlots;

        List<Exercise> result = new ArrayList<>();

        // 1. fill compound slots — highest scoring compounds first
        while (!compoundHeap.isEmpty() && result.size() < compoundSlots) {
            result.add(compoundHeap.poll());
        }

        // 2. fill isolation slots using priority order
        // ensures 1 tricep + 1 shoulder on push day instead of 2 triceps and 0 shoulders
        List<MuscleGroup> priority = ISOLATION_PRIORITY_MAP.get(workoutType);
        if (priority != null) {
            int filled = 0;
            for (MuscleGroup mg : priority) {
                if (filled >= isolationSlots) break;
                PriorityQueue<Exercise> heap = isolationHeaps.get(mg);
                if (heap != null && !heap.isEmpty()) {
                    result.add(heap.poll());
                    filled++;
                }
            }
        }

        return result;
    }

    /*
     * getMaxExercises
     * Returns how many exercises fit in a given number of minutes.
     * Useful for controllers that need to know the cap before calling buildQueue.
     *
     * @param availableMinutes the user's available time for this day
     * @return maximum number of exercises that will be returned
     */
    public static int getMaxExercises(int availableMinutes) {
        return availableMinutes / MINUTES_PER_EXERCISE;
    }

    private int scoreExercise(Exercise exercise) {
        if (currentWorkoutType == null) return 0;
        Set<MuscleGroup> targets = WORKOUT_CATEGORY_MAP.get(currentWorkoutType);
        if (targets == null || targets.isEmpty()) return 0;

        int score = 0;

        // score the exercise's primary muscle group
        if (targets.contains(exercise.getMuscle())) {
            score += PRIMARY_SCORE;
        }

        // score secondary muscles — a row hitting BACK + BICEPS is more relevant
        // on a PULL day than a back exercise with no bicep involvement
        for (TargetedMuscle tm : exercise.getSecondaryMuscles()) {
            for (MuscleGroup target : targets) {
                if (target.name().equalsIgnoreCase(tm.getMuscle())) {
                    score += SECONDARY_SCORE;
                }
            }
        }

        return score;
    }
}

