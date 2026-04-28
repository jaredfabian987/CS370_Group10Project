package com.repit.Services;

import com.repit.DAOs.FitnessProfileDAO;
import com.repit.DAOs.WorkoutLogsDAO;
import com.repit.Model.FitnessProfile;
import com.repit.Model.WorkoutLog;

import java.util.ArrayList;
import java.util.Set;
import java.time.LocalDate;

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

    /**
     * Returns how many workout days the user has completed so far this week (Mon-Sun).
     * A day counts as completed if the user logged at least one completed set on that date.
     * This is the "n completed" half of the dashboard weekly progress banner.
     *
     * Example: if today is Wednesday and the user trained Monday and Tuesday,
     * this returns 2.
     *
     * @param userId the logged-in user's ID
     * @return count of completed workout days this week
     */
    public int getWeeklyWorkoutsCompleted(int userId) {
        Set<LocalDate> completedDates = workoutLogsDAO.getLoggedDatesThisWeek(userId);
        return completedDates.size();
    }

    /**
     * Returns how many workout days the user has planned for this week.
     * Comes directly from the user's fitness profile (daysPerWeek).
     * This is the "n total" half of the dashboard weekly progress banner.
     *
     * Example: if the user set up a 4-day plan, this returns 4.
     * Paired with getWeeklyWorkoutsCompleted() to display "2 out of 4 completed".
     *
     * @param userId the logged-in user's ID
     * @return total planned training days per week, or 0 if no profile exists
     */
    public int getWeeklyWorkoutsPlanned(int userId) {
        FitnessProfile profile = fitnessProfileDAO.getProfile(userId);
        if (profile == null) return 0;
        return profile.getDaysPerWeek();
    }

    /**
     * Returns how many exercises the user has planned per workout session.
     * Derived from minsAvailablePerWorkout divided by 10 (each exercise = 10 min).
     * This is the "n total" for the per-session exercise progress counter.
     *
     * Example: 50 minutes available → 5 exercises planned per session.
     *
     * @param userId the logged-in user's ID
     * @return number of exercises per session, or 0 if no profile exists
     */
    public int getTotalExercisesPlannedPerSession(int userId) {
        FitnessProfile profile = fitnessProfileDAO.getProfile(userId);
        if (profile == null) return 0;
        // floor to the nearest 10 min block, each block = 1 exercise
        return (int)(profile.getMinsAvailablePerWorkout() / 10);
    }
}