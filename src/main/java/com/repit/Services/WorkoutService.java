package com.repit.Services;

import com.repit.DAOs.WorkoutLogsDAO;
import com.repit.util.SplitSelector;
import com.repit.util.WarmupCalculator;
import com.repit.Model.WorkoutLog;
import com.repit.Model.enums.WorkoutType;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * WorkoutService
 * Handles all workout-related business logic for the workout screen.
 *
 * Responsibilities:
 * - Reading and saving workout logs via WorkoutLogsDAO
 * - Delegating split selection to SplitSelector
 * - Delegating warmup calculation to WarmupCalculator
 *
 * Controllers never call SplitSelector or WarmupCalculator directly.
 * All workout logic is routed through this service.
 */
public class WorkoutService {

    private final WorkoutLogsDAO workoutLogsDAO;

    public WorkoutService(WorkoutLogsDAO workoutLogsDAO) {
        this.workoutLogsDAO = workoutLogsDAO;
    }

    // --- Workout Log Operations ---

    /**
     * Returns all workout logs for a user.
     */
    public ArrayList<WorkoutLog> getWorkoutLogs(int userId) {
        return workoutLogsDAO.getLogs(userId);
    }

    /**
     * Saves a single workout log entry.
     */
    public boolean saveWorkoutLog(WorkoutLog log) {
        return workoutLogsDAO.saveWorkoutLog(log);
    }

    /**
     * Deletes a workout log entry.
     */
    public boolean deleteWorkoutLog(int logId, int userId) {
        return workoutLogsDAO.deleteWorkoutLog(logId, userId);
    }

    // --- Split Selection ---

    /**
     * Returns the recommended workout split for a user's available days and goal.
     * Delegates to SplitSelector.
     *
     * @param availableDays the days the user can train
     * @param goal "MUSCLE_BUILDING" or "WEIGHT_LOSS"
     * @return ordered list of WorkoutType for each training day
     */
    public List<WorkoutType> getSplit(Set<DayOfWeek> availableDays, String goal) {
        return SplitSelector.selectSplit(availableDays, goal);
    }

    // --- Warmup Calculation ---

    /**
     * Returns two warmup weights for a weighted exercise.
     * Delegates to WarmupCalculator.
     *
     * @param workingWeight the weight the user lifts for working sets
     * @return [set1Weight, set2Weight] both rounded to the nearest 5lbs
     */
    public List<Double> getWarmups(double workingWeight) {
        return WarmupCalculator.calculateWarmups(workingWeight);
    }

    /**
     * Returns two warmup rep counts for a bodyweight exercise.
     * Delegates to WarmupCalculator.
     *
     * @param workingReps the reps the user does for working sets
     * @return [set1Reps, set2Reps] both rounded to the nearest whole rep
     */
    public List<Integer> getWarmupsBodyweight(int workingReps) {
        return WarmupCalculator.calculateWarmupsBodyweight(workingReps);
    }

    /**
     * Returns true if the user is still in calibration week.
     * Warmup sets are skipped during calibration week.
     * Delegates to WarmupCalculator.
     *
     * @param isCalibrated the calibration flag from FitnessProfile
     * @return true if calibration week is still active
     */
    public boolean isCalibrationWeek(boolean isCalibrated) {
        return WarmupCalculator.isCalibrationWeek(isCalibrated);
    }
}
