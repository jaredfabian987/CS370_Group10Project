package com.repit.Services;
import com.repit.Model.enums.WorkoutType;
import java.time.DayOfWeek;
import java.util.*;

/**
 * Split Selector:
 * Determines the workout split based on days available per week
 * A split is how you divide muscle group across workout days
 *
 * Supported goals:
 *  - muscle building: focus on strength training splits
 *  - weight loss: mix of strength training and cardio
 * Examples:
 * - 3 days = upper, lower, full body
 * - 6 days = push, pull, legs, push, pull legs
 */
public class SplitSelector {




    // select optional workout based on available days
    /**
     * @param availableDays is the set of days that the user can workout
     * @param goal is the user's fitness goal
     * @return ordered List<WorkoutType> is an order list of the workout type for each day
     * @throws IllegalArgumentException if the goal is not recognized
     *
     * in order for proper muscle growth, the recommended frequency per muscle group
     * is to workout each group at least twice per week, given this
     *
     * muscle building strategy:
     * - focus on strength training, compounds movements, progressive overload, minimal cardio
     *
     * weight loss strategy
     * - mix strength & cardio
     * - higher frequency cardio
     * - maintain muscle while losing fat
     * - more total calories burned
     *
     * some examples:
     * selectSplit({Mon,WEd, Fri}), "MUSCLE BUILDING")
     * result =  [upper, lower, full body]
     *
     * selectSplit({MON, WED, FRI}, "WEIGHT_LOSS")
     * result = [UPPER, LOWER, CARDIO]
     */


    public static List <WorkoutType> selectSplit (Set<DayOfWeek> availableDays, String goal){
        // validate goal up front so callers get a clear error instead of
        // having to default one goal or the other
        if (goal == null ||
                (!goal.equalsIgnoreCase("MUSCLE_BUILDING") &&
                        !goal.equalsIgnoreCase("WEIGHT_LOSS"))){
            throw new IllegalArgumentException("Unrecognized goal: \"" + goal + "\". Expected \"MUSCLE_BUILDING\" or \"WEIGHT_LOSS\".");
        }

        // validate days to make it is not 0
        if (availableDays == null || availableDays.isEmpty()) {
            throw new IllegalArgumentException(
                    "availableDays cannot be null or empty. " +
                            "User must have at least 1 available training day."
            );
        }

        int days = availableDays.size();
        boolean isMuscle = goal.equalsIgnoreCase("MUSCLE_BUILDING");


        switch (days) {
            case 1:
                // if only one day is available - full body
                // is the way to stimulate major muscle groups in a single sessions
                // is the same for goals
                return Collections.singletonList(WorkoutType.FULL_BODY);
            case 2:
                // if two days are available then U/L is the most efficient 2 day split
                // each session covers one half of the body which will give the every muscle group
                // one stimulus per week
                return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER);
            case 3:
                // if the goal is build muscle then one upper + one lower + one full body
                // day will give the needed twice=weekly frequency for hypertrophy
                if (isMuscle){
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER, WorkoutType.FULL_BODY);
                } else {
                    // if the goal is to lose weight then replace the full body day with a dedicated
                    // cardio day which would maximize calories burned
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER, WorkoutType.CARDIO);
                }
            case 4:
                if (isMuscle) {
                    // if the goal it to build muscle then U/L x2 hits every muscle group twice per week
                    // which is the sweet spot to build muscle
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER,
                            WorkoutType.UPPER, WorkoutType.LOWER);
                } else {
                    // two strength days to maintain muscle and two cardio days
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER,
                            WorkoutType.CARDIO, WorkoutType.CARDIO);
                }
            case 5:
                if (isMuscle) {
                    // PPL on days 1-3 and then U/L on days
                    // 4-5 which hits all muscle groups at least twice a week
                    // but keep the volume a little bit more manageable
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.UPPER, WorkoutType.LOWER);
                } else {
                    // there are two strength main muscle mass
                    // and two cardio days for weight loss
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.CARDIO, WorkoutType.CARDIO);
                }
            case 6:
                if (isMuscle) {
                    // we are going to recommend PPL x2 a week which trains every muscle group
                    // is trained twice a week for optimal growth
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS);
                } else {
                    // we are going to recommend UL x2 and Cardio x2 this will make sure every muscle
                    // group gets worked at least twice a week, and we still get cardio at least 2x a week
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER, WorkoutType.UPPER, WorkoutType.LOWER,
                            WorkoutType.CARDIO, WorkoutType.CARDIO);
                }
            default:
                // anything more than 6 days get capped at the 6-day split to enforce at leasee one rest days
                // training for 7 day sin a row can be counterproductive because muscles grow from recovery
                if (isMuscle) {
                    // we are going to recommend PPL x2 a week which trains every muscle group
                    // is trained twice a week for optimal growth
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS);
                } else {
                    // we are going to recommend UL x2 and Cardio x2 this will make sure every muscle
                    // group gets worked at least twice a week, and we still get cardio at least 2x a week
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.CARDIO, WorkoutType.CARDIO, WorkoutType.FULL_BODY);
                }
        }
    }

}