package com.repit.Services;

import com.repit.DAOs.WorkoutLogsDAO;
import com.repit.Model.Exercise;
import com.repit.Model.ProgressionSuggestion;
import com.repit.Model.WorkoutLog;

import java.util.ArrayList;

/**
 * ProgressService
 * Determines whether a user has met the progressive overload threshold
 * and returns the suggested weight and reps for their next session.
 *
 * Progressive overload rules:
 * - Weighted exercise: if reps >= 10, increase weight using the Epley formula
 *   targeting 7 reps (midpoint of the 6-8 rep range), rounded to nearest 5lbs.
 * - Bodyweight exercise: if reps >= 10, add 2 reps, no weight change.
 * - Below threshold: return same weight and reps — no change yet.
 */
public class ProgressService {

    private static final int PROGRESSION_THRESHOLD = 10;
    private static final int TARGET_REPS = 7;
    private static final int BODYWEIGHT_REP_INCREMENT = 2;
    private static final double ROUND_TO = 5.0;

    private final WorkoutLogsDAO workoutLogsDAO;

    public ProgressService(WorkoutLogsDAO workoutLogsDAO) {
        this.workoutLogsDAO = workoutLogsDAO;
    }

    /**
     * Returns the suggested weight and reps for the user's next session on a given exercise.
     * Finds the most recent log for that exercise, checks if the threshold is met,
     * and applies the appropriate progression rule.
     *
     * @param userId     the logged-in user's ID
     * @param exerciseId the exercise to check progression for
     * @param exercise   the Exercise object (used to determine weighted vs bodyweight)
     * @return ProgressionSuggestion with next weight/reps, or null if no history exists
     */
    public ProgressionSuggestion suggestProgression(int userId, int exerciseId, Exercise exercise) {
        ArrayList<WorkoutLog> logs = workoutLogsDAO.getLogs(userId);
        if (logs == null || logs.isEmpty()) return null;

        WorkoutLog lastLog = null;
        for (int i = logs.size() - 1; i >= 0; i--) {
            if (logs.get(i).getExerciseId() == exerciseId) {
                lastLog = logs.get(i);
                break;
            }
        }

        if (lastLog == null) return null;

        int reps = lastLog.getReps();
        double weight = lastLog.getWeight();

        if (reps < PROGRESSION_THRESHOLD) {
            return new ProgressionSuggestion(weight, reps, false);
        }

        if (exercise.isBodyweight()) {
            return new ProgressionSuggestion(0, reps + BODYWEIGHT_REP_INCREMENT, true);
        }

        // Epley 1RM formula: 1RM = weight * (1 + reps / 30)
        // back-calculate weight for TARGET_REPS: 1RM / (1 + TARGET_REPS / 30)
        double oneRepMax = weight * (1 + reps / 30.0);
        double rawNext = oneRepMax / (1 + TARGET_REPS / 30.0);
        double nextWeight = roundToNearest(rawNext, ROUND_TO);

        return new ProgressionSuggestion(nextWeight, TARGET_REPS, true);
    }

    /**
     * Returns how many distinct exercises the user has completed today.
     * This is the "n completed" part of the "n out of n" dashboard progress counter.
     *
     * Example: if the user has logged bench press and shoulder press today,
     * this returns 2 — regardless of how many sets they did for each.
     *
     * @param userId the logged-in user's ID
     * @return count of distinct exercises completed today
     */
    public int getCompletedExerciseCountToday(int userId) {
        return workoutLogsDAO.getCompletedExercisesCountToday(userId);
    }

    private static double roundToNearest(double value, double increment) {
        return Math.round(value / increment) * increment;
    }
}
