package com.repit.util;

import com.repit.Model.Exercise;
import com.repit.Model.TargetedMuscle;
import com.repit.Model.enums.MuscleGroup;
import com.repit.Model.enums.WorkoutType;

import java.util.*;

/**
 * ExercisePriorityQueue
 * .
 * Ranks and orders exercises for a given workout day based on how well they
 * match the day's target muscles and how much time the user has available.
 *
 * Scoring per exercise:
 * - Primary muscle match (exercise.getMuscle()) -> +2 points
 * - Secondary muscle match (exercise.getSecondaryMuscles()) -> +1 point per match
 * Exercises with a score of 0 are excluded for example:
 * A leg exercise like a squat would get a score of 0 on a push day because iit is not the intended target
 * muscle for that day
 *
 * Time budget — each exercise costs 10 minutes (2 warmup + 2 working sets + rest):
 * - 30 min = 3 exercises: 2 compounds + 1 isolation
 * - 40 min = 4 exercises: 2 compounds + 2 isolations
 * - 50 min = 5 exercises: 3 compounds + 2 isolations
 * - 60 min = 6 exercises: 4 compounds + 2 isolations
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

    /**
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

        // scoreExercise() uses this to know which muscles to compare against
        this.currentWorkoutType = workoutType;

        // clear heaps from any previous call so stale exercises don't carry over
        compoundHeap.clear();
        for (PriorityQueue<Exercise> heap : isolationHeaps.values()) heap.clear();

        // score every exercise — anything with score 0 is irrelevant for this workout type and gets skipped
        // compounds go into one shared heap; isolations go into a per-muscle-group heap
        for (Exercise exercise : exercises) {
            if (scoreExercise(exercise) > 0) {
                if (exercise.isCompound()) {
                    // compounds compete against each other as one group
                    compoundHeap.offer(exercise);
                } else {
                    // isolations are bucketed by muscle so we can pick the best tricep
                    // separately from the best shoulder instead of mixing them together
                    PriorityQueue<Exercise> heap = isolationHeaps.get(exercise.getMuscle());
                    if (heap != null) heap.offer(exercise);
                }
            }
        }

        // figure out how many total exercise slots fit in the time budget
        int totalSlots = availableMinutes / MINUTES_PER_EXERCISE;

        // isolation slots are capped at MAX_ISOLATION (2) — the rest go to compounds
        // if we only have enough time for MIN_COMPOUND exercises, give all slots to compounds
        int isolationSlots = totalSlots > MIN_COMPOUND
                ? Math.min(MAX_ISOLATION, totalSlots - MIN_COMPOUND) : 0;
        int compoundSlots = totalSlots - isolationSlots;

        List<Exercise> result = new ArrayList<>();

        // 1. fill compound slots first — the heap is sorted highest score first,
        //    so poll() always gives the best remaining compound
        while (!compoundHeap.isEmpty() && result.size() < compoundSlots) {
            result.add(compoundHeap.poll());
        }

        // 2. fill isolation slots using the priority order for this workout type
        //    each MuscleGroup in the list gets one slot — this guarantees variety
        //    (e.g. push day gets 1 tricep + 1 shoulder, never 2 triceps)
        List<MuscleGroup> priority = ISOLATION_PRIORITY_MAP.get(workoutType);
        if (priority != null) {
            int filled = 0;
            for (MuscleGroup mg : priority) {
                if (filled >= isolationSlots) break;             // all isolation slots used
                PriorityQueue<Exercise> heap = isolationHeaps.get(mg);
                if (heap != null && !heap.isEmpty()) {
                    result.add(heap.poll());                     // best exercise for this muscle group
                    filled++;
                }
            }
        }

        return result;
    }

    /**
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
        // can't score without knowing the workout type
        if (currentWorkoutType == null) {
            return 0;
        }

        // look up which muscle groups this workout type cares about
        Set<MuscleGroup> targets = WORKOUT_CATEGORY_MAP.get(currentWorkoutType);

        // REST, CARDIO, CORE have empty target sets — nothing scores above 0
        if (targets == null || targets.isEmpty()) {
            return 0;}

        int score = 0;

        // +2 if the exercise's main muscle is a target for today (e.g. bench press → CHEST on push day)
        if (targets.contains(exercise.getMuscle())) {
            score += PRIMARY_SCORE;
        }

        // +1 for each secondary muscle that also matches a target
        // this helps rank exercises that hit multiple relevant muscles higher
        // e.g. a barbell row hitting BACK (primary) + BICEPS (secondary) scores higher on pull day
        //      than a row with no bicep involvement
        // defensive: Exercise.getSecondaryMuscles() should always return a list,
        // but if it ever returns null (e.g. legacy data, partially-built Exercise),
        // skip the secondary-muscle bonus rather than crash the planner
        List<TargetedMuscle> secondary = exercise.getSecondaryMuscles();
        if (secondary == null) secondary = java.util.Collections.emptyList();
        for (TargetedMuscle tm : secondary) {
            for (MuscleGroup target : targets) {
                if (target.name().equalsIgnoreCase(tm.getMuscle())) {
                    score += SECONDARY_SCORE;
                }
            }
        }

        // score of 0 means this exercise has nothing to do with today's workout type — it gets filtered out
        return score;
    }
}

