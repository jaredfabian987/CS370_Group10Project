package com.repit.Services;

import com.repit.DAOs.FitnessProfileDAO;
import com.repit.DAOs.WorkoutLogsDAO;
import com.repit.Model.FitnessProfile;
import com.repit.Model.WorkoutLog;

import java.util.ArrayList;

/**
 * DashboardService
 * Aggregates data needed to render the home/dashboard screen.
 *
 * Responsibilities:
 * - Returning the user's most recent workout
 * - Returning a total workout count for the summary banner
 * - Returning the fitness profile summary for the dashboard header
 *
 * Controllers never query DAOs directly for dashboard data.
 * All dashboard aggregation lives here.
 */
public class DashboardService {

    private final WorkoutLogsDAO workoutLogsDAO;
    private final FitnessProfileDAO fitnessProfileDAO;

    public DashboardService(WorkoutLogsDAO workoutLogsDAO, FitnessProfileDAO fitnessProfileDAO) {
        this.workoutLogsDAO = workoutLogsDAO;
        this.fitnessProfileDAO = fitnessProfileDAO;
    }

    /**
     * Returns the user's most recent workout log entry.
     * Used to show the "last workout" summary card on the dashboard.
     *
     * @param userId the logged-in user's ID
     * @return the most recent WorkoutLog, or null if no workouts logged yet
     */
    public WorkoutLog getLastWorkout(int userId) {
        ArrayList<WorkoutLog> logs = workoutLogsDAO.getLogs(userId);
        if (logs == null || logs.isEmpty()) return null;
        return logs.get(logs.size() - 1);
    }

    /**
     * Returns the total number of workout log entries for a user.
     * Shown as a quick stat in the dashboard summary banner.
     *
     * @param userId the logged-in user's ID
     * @return total workout count
     */
    public int getWorkoutCount(int userId) {
        ArrayList<WorkoutLog> logs = workoutLogsDAO.getLogs(userId);
        return logs == null ? 0 : logs.size();
    }

    /**
     * Returns the user's fitness profile for display in the dashboard header.
     * If this returns null the controller should redirect to the setup screen.
     *
     * @param userId the logged-in user's ID
     * @return the user's FitnessProfile, or null if setup is not complete
     */
    public FitnessProfile getProfileSummary(int userId) {
        return fitnessProfileDAO.getProfile(userId);
    }
}
