package com.repit.util;

import com.repit.Model.enums.WorkoutType;

import java.time.DayOfWeek;
import java.util.*;

/**
 * SplitSelector
 * Pure utility — no database access, no state.
 * Determines the optimal workout split based on how many days per week
 * the user can train and what their fitness goal is.
 *
 * A "split" is how muscle groups are divided across training days.
 *
 * Supported goals: "MUSCLE_BUILDING" or "WEIGHT_LOSS"
 *
 * Muscle building strategy:
 * - Prioritize strength training and compound movements
 * - Hit each muscle group at least twice per week for hypertrophy
 * - Minimal dedicated cardio
 *
 * Weight loss strategy:
 * - Mix strength and cardio to maximize calorie burn
 * - Maintain muscle mass while in a caloric deficit
 * - Replace some strength days with cardio days
 *
 * Examples:
 * selectSplit({MON, WED, FRI}, "MUSCLE_BUILDING") -> [UPPER, LOWER, FULL_BODY]
 * selectSplit({MON, WED, FRI}, "WEIGHT_LOSS") -> [UPPER, LOWER, CARDIO]
 */
public class SplitSelector {

    /*
     * selectSplit
     * Returns an ordered list of WorkoutType values, one per available training day.
     * The order matches the recommended training sequence for that split.
     *
     * @param availableDays the days the user can train
     * @param goal "MUSCLE_BUILDING" or "WEIGHT_LOSS"
     * @return ordered list of WorkoutType for each training day
     * @throws IllegalArgumentException if goal is unrecognized or availableDays is empty
     */
    public static List<WorkoutType> selectSplit(Set<DayOfWeek> availableDays, String goal) {

        if (goal == null ||
                (!goal.equalsIgnoreCase("MUSCLE_BUILDING") &&
                        !goal.equalsIgnoreCase("WEIGHT_LOSS"))) {
            throw new IllegalArgumentException(
                    "Unrecognized goal: \"" + goal + "\". Expected \"MUSCLE_BUILDING\" or \"WEIGHT_LOSS\".");
        }

        if (availableDays == null || availableDays.isEmpty()) {
            throw new IllegalArgumentException("availableDays cannot be null or empty.");
        }

        int days = availableDays.size();
        boolean isMuscle = goal.equalsIgnoreCase("MUSCLE_BUILDING");

        switch (days) {
            case 1:
                return Collections.singletonList(WorkoutType.FULL_BODY);

            case 2:
                return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER);

            case 3:
                if (isMuscle) {
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER, WorkoutType.FULL_BODY);
                } else {
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER, WorkoutType.CARDIO);
                }

            case 4:
                if (isMuscle) {
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER,
                            WorkoutType.UPPER, WorkoutType.LOWER);
                } else {
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER,
                            WorkoutType.CARDIO, WorkoutType.CARDIO);
                }

            case 5:
                if (isMuscle) {
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.UPPER, WorkoutType.LOWER);
                } else {
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.CARDIO, WorkoutType.CARDIO);
                }

            case 6:
                if (isMuscle) {
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS);
                } else {
                    return Arrays.asList(WorkoutType.UPPER, WorkoutType.LOWER,
                            WorkoutType.UPPER, WorkoutType.LOWER,
                            WorkoutType.CARDIO, WorkoutType.CARDIO);
                }

            default:
                // Cap at 6-day split — muscles need at least one rest day to recover
                if (isMuscle) {
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS);
                } else {
                    return Arrays.asList(WorkoutType.PUSH, WorkoutType.PULL, WorkoutType.LEGS,
                            WorkoutType.CARDIO, WorkoutType.CARDIO, WorkoutType.FULL_BODY);
                }
        }
    }
}
