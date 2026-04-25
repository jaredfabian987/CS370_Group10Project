package com.repit.util;

import com.repit.Model.Exercise;
import com.repit.Model.TargetedMuscle;
import com.repit.Model.enums.MuscleGroup;
import com.repit.Model.enums.WorkoutType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


/**
 * ExercisePriorityQueue
 * Ranks and order exercises dynamically based on how well they match the current workout day's target
 * and how much time the user has available
 *
 * Examples:
 * A lat pull-down is a high priority movement for a PULL day but irrelevant on a PUSH day.
 * I was thinking of hardcoding these values but i think that it would ignore some of th context
 *
 * The scoring works that for each exercise: each primary muscle that matches that day's target muscles = +2 points
 * each secondary muscle that matches the day's target muscle = +1 point
 * Primary muscles are worth more than secondary because they are the ones that are doing most of the
 * work in the movement.
 * An exercise where the target muscle is more efficient than one where it only appears as a secondary mover.
 *
 * Time also affect exercise selection:
 * Each exercise take approximately 10 minutes:
 *
 * - 30 min = 3 exercises → 2 compounds, 1 isolation
 * - 40 min = 4 exercises → 2 compounds, 2 isolation
 * - 50 min = 5 exercises → 3 compounds, 2 isolations
 * - 60 min = 6 exercises → 4 compounds, 2 isolations
 *
 * 2 isolation exercises regardless of time
 * - isolation movement have diminishing returns after 2 exercise because you could spend your time better on
 *   movements
 *
 * How isolation slots are distributed per day:
 * isolation slots are filled up per target category in priori order so we never get
 * 2 triceps isolation movements and 0 shoulder isolations on a push day
 *
 * Push isolation priority -> [TRICEPS, SHOULDERS]
 * slot 1 is for best tricep isolation like a tricep pushdown
 * slot 2 is for shoulder isolation like a lateral raise
 *
 * Pull day isolation priority -> [BICEPS, BICEPS]
 * slot 1 is for bicep isolation like a preacher curl
 * slot 2 is for the next bicep isolation like a hammer curl
 *
 * Legs isolation priority [QUADS, HAMSTRINGS]
 * slot 1 for quad isolation like leg extension
 * slot 2 for hamstring isolation like a leg curl
 *
 * Example push day 40 minutes with 2 compounds and 2 isolation
 * compounds: Bench Press, Overhead Press
 * isolations: tricep pushdowns, lateral raise
 *
 * Example pull day 50 minutes 3 compounds and 2 isolation
 * compounds: lat pull-down, barbell row, seated cable row
 * isolation: hammer curl, preacher curl
 */
public class ExercisePriorityQueue {

    // each exercise 10 minutes including warm up sets,
    // working sets, and rest periods between sets
    private static final int MINUTES_PER_EXERCISE = 10;

    // points awarded for muscle category matches
    private static final int PRIMARY_SCORE = 2;
    private static final int SECONDARY_SCORE = 1;

    // 2 isolations max as stated before
    // extra time is better spent on more compound work
    private static final int MAX_ISOLATION_EXERCISE = 2;

    // minimum compounds we always want regardless of time
    private static final int MIN_COMPOUND_EXERCISE = 2;

    /*
     * Workout Category Map
     *
     * Maps each WorkoutType to the MuscleGroup values it targets.
     * Used to score each exercise against the day's workout type
     */
    private static final Map<WorkoutType, Set<MuscleGroup>> WORKOUT_CATEGORY_MAP = new HashMap<>();

    /*
     * Isolation Priority Map
     *
     * Maps each WorkoutType to an ordered list of isolation of categories
     *
     * This matters because we want triceps before shoulders on a push day
     * We fill one slot per category in this exact order to avoid ending up with
     * something like : 2 tricep isolation exercises and 0 shoulder isolation exercises on a push day
     */
    private static final Map<WorkoutType, List<MuscleGroup>> ISOLATION_PRIORITY_MAP = new HashMap<>();

    static {

        // push day: chest, shoulders, tris
        Set<MuscleGroup> pushMuscles = new HashSet<>();
        pushMuscles.add(MuscleGroup.CHEST);
        pushMuscles.add(MuscleGroup.SHOULDERS);
        pushMuscles.add(MuscleGroup.TRICEPS);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.PUSH, pushMuscles);

        // pull day: back and bis
        Set<MuscleGroup> pullMuscles = new HashSet<>();
        pullMuscles.add(MuscleGroup.BACK);
        pullMuscles.add(MuscleGroup.BICEPS);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.PULL, pullMuscles);

        // leg day
        Set<MuscleGroup> legMuscles = new HashSet<>();
        legMuscles.add(MuscleGroup.QUADS);
        legMuscles.add(MuscleGroup.HAMSTRINGS);
        legMuscles.add(MuscleGroup.GLUTES);
        legMuscles.add(MuscleGroup.CALVES);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.LEGS, legMuscles);

        // upper day - push + pull combined
        Set<MuscleGroup> upperMuscles = new HashSet<>();
        upperMuscles.addAll(pushMuscles);
        upperMuscles.addAll(pullMuscles);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.UPPER, upperMuscles);

        // lower day is just the same as legs
        Set<MuscleGroup> lowerMuscles = new HashSet<>(legMuscles);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.LOWER, lowerMuscles);

        // full body is just everything
        Set<MuscleGroup> fullBodyMuscles = new HashSet<>(upperMuscles);
        fullBodyMuscles.addAll(lowerMuscles);
        WORKOUT_CATEGORY_MAP.put(WorkoutType.FULL_BODY, fullBodyMuscles);

        // cardio and rest no muscle scoring needed
        // cardio and rest days never reach the exercise heap
        // workout generator handles them before calling buildQueue()
        WORKOUT_CATEGORY_MAP.put(WorkoutType.CARDIO, new HashSet<>());
        WORKOUT_CATEGORY_MAP.put(WorkoutType.REST, new HashSet<>());

        // isolation priority map

        // PUSH — tricep first then shoulder
        // triceps are the smaller muscle group and benefit most
        // from dedicated isolation work after compound pressing
        List<MuscleGroup> pushIsolation = new ArrayList<>();
        pushIsolation.add(MuscleGroup.TRICEPS);
        pushIsolation.add(MuscleGroup.SHOULDERS);
        ISOLATION_PRIORITY_MAP.put(WorkoutType.PUSH, pushIsolation);

        // PULL — biceps for both slots
        // WHY both slots to biceps:
        // back is already covered by compound rows and pulldowns
        // both isolation slots go to bicep variations
        List<MuscleGroup> pullIsolation = new ArrayList<>();
        pullIsolation.add(MuscleGroup.BICEPS);
        pullIsolation.add(MuscleGroup.BICEPS);
        ISOLATION_PRIORITY_MAP.put(WorkoutType.PULL, pullIsolation);

        // LEGS — quad first then hamstring
        // quads are typically the main focus of leg day
        // and benefit most from a dedicated isolation like leg extension
        List<MuscleGroup> legsIsolation = new ArrayList<>();
        legsIsolation.add(MuscleGroup.QUADS);
        legsIsolation.add(MuscleGroup.HAMSTRINGS);
        ISOLATION_PRIORITY_MAP.put(WorkoutType.LEGS, legsIsolation);

        // UPPER: tricep then bicep
        List<MuscleGroup> upperIsolation = new ArrayList<>();
        upperIsolation.add(MuscleGroup.TRICEPS);
        upperIsolation.add(MuscleGroup.BICEPS);
        ISOLATION_PRIORITY_MAP.put(WorkoutType.UPPER, upperIsolation);

        // LOWER is same as legs
        List<MuscleGroup> lowerIsolation = new ArrayList<>();
        lowerIsolation.add(MuscleGroup.QUADS);
        lowerIsolation.add(MuscleGroup.HAMSTRINGS);
        ISOLATION_PRIORITY_MAP.put(WorkoutType.LOWER, lowerIsolation);

        // FULL_BODY  tricep then bicep
        List<MuscleGroup> fullBodyIsolation = new ArrayList<>();
        fullBodyIsolation.add(MuscleGroup.TRICEPS);
        fullBodyIsolation.add(MuscleGroup.BICEPS);
        ISOLATION_PRIORITY_MAP.put(WorkoutType.FULL_BODY, fullBodyIsolation);
    }

    // the current workout type set in buildQueue()
    // used by scoreExercise() to know which muscles to match against
    private WorkoutType currentWorkoutType;

    // compound heap always filled first regardless of score
    // WHY separate from isolation:
    // compounds always come before isolations — mixing them in one heap
    // could let a high scoring isolation jump ahead of a lower scoring
    // compound which we never want
    private final PriorityQueue<Exercise> compoundHeap;

    // one isolation heap per muscle group category
    // WHY one heap per category:
    // lets us pick the best tricep isolation and best shoulder
    // isolation independently instead of competing against each other
    private final Map<MuscleGroup, PriorityQueue<Exercise>> isolationHeaps;

    /**
     * Constructor
     * Initializes the compound heap and isolation heap map.
     * Call buildQueue() to load and rank exercises for a specific day.
     */
    public ExercisePriorityQueue() {
        this.compoundHeap = new PriorityQueue<>(
                (a, b) -> scoreExercise(b) - scoreExercise(a)
        );
        this.isolationHeaps = new HashMap<>();

        // pre-build one heap per muscle group category
        for (MuscleGroup muscleGroup : MuscleGroup.values()) {
            isolationHeaps.put(muscleGroup,
                    new PriorityQueue<>((a, b) -> scoreExercise(b) - scoreExercise(a))
            );
        }
    }

    /**
     * buildQueue
     * Loads exercises into the appropriate heap based on isCompound(),
     * scores each one against the workout type, then returns an ordered
     * list that respects both the time limit and compound/isolation split.
     *
     * @param exercises is the full list of exercises for this day
     * @param workoutType is the the type of workout day (PUSH, PULL, LEGS etc.)
     * @param availableMinutes how many minutes the user has today, must be a positive multiple of 10
     * @return ordered List<Exercise> compounds first then isolations
     * in category priority order fitting within available time
     * @throws IllegalArgumentException if inputs are invalid
     *
     * Example — PUSH day 40 minutes (2 compound + 2 isolation):
     *   result = [Bench Press, Overhead Press,
     *             Tricep Pushdown, Lateral Raise]
     *
     * Example — PULL day 50 minutes (3 compound + 2 isolation):
     *   result = [Lat Pulldown, Barbell Row, Seated Cable Row,
     *             Hammer Curl, Preacher Curl]
     */
    public List<Exercise> buildQueue(List<Exercise> exercises,
                                     WorkoutType workoutType, int availableMinutes) {

        // validate exercises
        if (exercises == null || exercises.isEmpty()) {
            throw new IllegalArgumentException(
                    "exercises cannot be null or empty."
            );
        }

        // validate workout type
        if (workoutType == null) {
            throw new IllegalArgumentException(
                    "workoutType cannot be null."
            );
        }

        // validate available minutes  must be a positive multiple of 15
        // each exercise takes exactly 15 minutes so any time that
        // is not a multiple of 10 would leave a partial exercise slot
        if (availableMinutes <= 0 || availableMinutes % MINUTES_PER_EXERCISE != 0) {
            throw new IllegalArgumentException(
                    "availableMinutes must be a positive multiple of "
                            + MINUTES_PER_EXERCISE + ", got: " + availableMinutes
            );
        }

        // set workout type so scoreExercise() knows what to match against
        this.currentWorkoutType = workoutType;

        // clear all heaps from any previous call
        compoundHeap.clear();
        for (PriorityQueue<Exercise> heap : isolationHeaps.values()) {
            heap.clear();
        }

        // load each exercise into the correct heap
        // skip exercises with score 0 ,  they don't belong on this day
        for (Exercise exercise : exercises) {
            if (scoreExercise(exercise) > 0) {
                if (exercise.isCompound()) {
                    compoundHeap.offer(exercise);
                } else {
                    // isolation goes into its muscle group specific heap
                    PriorityQueue<Exercise> categoryHeap =
                            isolationHeaps.get(exercise.getMuscle());
                    if (categoryHeap != null) {
                        categoryHeap.offer(exercise);
                    }
                }
            }
        }

        // calculate total exercise slots from available time
        int totalSlots = availableMinutes / MINUTES_PER_EXERCISE;

        // calculate isolation slots
        // never more than 2 isolations regardless of time available
        int isolationSlots = 0;
        if (totalSlots > MIN_COMPOUND_EXERCISE) {
            isolationSlots = Math.min(
                    MAX_ISOLATION_EXERCISE,
                    totalSlots - MIN_COMPOUND_EXERCISE
            );
        }

        // remaining slots go to compounds
        int compoundSlots = totalSlots - isolationSlots;

        // build the final ordered list
        List<Exercise> result = new ArrayList<>();

        // 1. fill compound slots first — highest scoring compounds first
        while (!compoundHeap.isEmpty() && result.size() < compoundSlots) {
            result.add(compoundHeap.poll());
        }

        // 2. fill isolation slots using category priority order
        // ensures we get 1 tricep + 1 shoulder on push day
        // instead of 2 triceps and 0 shoulders
        List<MuscleGroup> isolationPriority =
                ISOLATION_PRIORITY_MAP.get(workoutType);

        if (isolationPriority != null) {
            int isolationCount = 0;
            for (MuscleGroup muscleGroup : isolationPriority) {
                if (isolationCount >= isolationSlots) {
                    break;
                }
                // get the best available exercise for this muscle group
                PriorityQueue<Exercise> categoryHeap =
                        isolationHeaps.get(muscleGroup);
                if (categoryHeap != null && !categoryHeap.isEmpty()) {
                    result.add(categoryHeap.poll());
                    isolationCount++;
                }
            }
        }

        return result;
    }

    /**
     * scoreExercise
     * Calculates a relevance score for an exercise based on how well
     * its MuscleGroup matches the current workout type's targets.
     *
     * @param exercise the exercise to score
     * @return integer score, higher means higher priority,
     *         0 means this exercise does not belong on today's workout
     */
    private int scoreExercise(Exercise exercise) {
        // protect against scoreExercise being called before buildQueue
        if (currentWorkoutType == null) {
            return 0;
        }

        // get target muscle groups for today's workout type
        Set<MuscleGroup> targetMuscles =
                WORKOUT_CATEGORY_MAP.get(currentWorkoutType);

        // if no targets defined (e.g. CARDIO, REST) return 0
        if (targetMuscles == null || targetMuscles.isEmpty()) {
            return 0;
        }

        int score = 0;

        // score the exercise's primary muscle group
        // the category field represents the main muscle group
        // the exercise is designed to target
        if (targetMuscles.contains(exercise.getMuscle())) {
            score += PRIMARY_SCORE;
        }

        // score secondary muscles against target muscle groups
        // a barbell row has BACK as its category but also hits BICEPS
        // on a PULL day this makes it even more relevant than a back
        // exercise with no bicep involvement at all
        for (TargetedMuscle secondary : exercise.getSecondaryMuscles()) {
            for (MuscleGroup targetMuscle : targetMuscles) {
                if (targetMuscle.name().equalsIgnoreCase(secondary.getMuscle())) {
                    score += SECONDARY_SCORE;
                }
            }
        }

        return score;
    }

    /**
     * getMaxExercises
     * Returns how many exercises fit in a given number of minutes.
     * Useful for WorkoutGenerator to check capacity before calling buildQueue.
     *
     * @param availableMinutes the number of minutes available
     * @return the number of exercises that fit in that time
     */
    public static int getMaxExercises(int availableMinutes) {
        return availableMinutes / MINUTES_PER_EXERCISE;
    }
}