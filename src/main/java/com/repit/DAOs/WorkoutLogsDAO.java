package com.repit.DAOs;

import com.repit.Model.WorkoutLog;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WorkoutLogsDAO extends BaseDAO {

    /*
     * reps and weight are included so ProgressService can read the last logged
     * performance and decide whether to suggest a weight increase next session.
     */
    private static final String TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS workout_logs (" +
                    "logId INTEGER PRIMARY KEY," +
                    "userId INTEGER NOT NULL," +
                    "exerciseId INTEGER NOT NULL," +
                    "isCompleted INTEGER NOT NULL," +
                    "date TEXT NOT NULL," +
                    "reps INTEGER NOT NULL DEFAULT 0," +
                    "weight REAL NOT NULL DEFAULT 0" +
                    ")";

    private static final String INSERT_SQL =
            "INSERT INTO workout_logs (userId, exerciseId, isCompleted, date, reps, weight) " +
                    "VALUES (?,?,?,?,?,?)";

    private static final String SELECT_BY_USER_SQL =
            "SELECT * FROM workout_logs WHERE userId = ?";

    /*
     * Returns only the single most recent log for a specific user + exercise pair.
     * Used by ProgressService to determine the last recorded weight and reps
     * before deciding whether to suggest a progression.
     */
    private static final String SELECT_LAST_LOG_SQL =
            "SELECT * FROM workout_logs WHERE userId = ? AND exerciseId = ? " +
                    "ORDER BY logId DESC LIMIT 1";

    /*
     * Returns distinct dates this week (Mon-Sun) where the user logged at least
     * one completed set. Used by the dashboard to show "X / Y workouts" and mark
     * day cards as Completed vs Scheduled.
     */
    private static final String SELECT_WEEK_DATES_SQL =
            "SELECT DISTINCT date FROM workout_logs " +
                    "WHERE userId = ? AND date >= ? AND date <= ? AND isCompleted = 1";

    /*
     * Counts how many distinct exercises the user has completed today.
     * "Completed" means isCompleted = 1 — a set was successfully logged.
     * We count DISTINCT exerciseId so logging multiple sets of bench press
     * only counts as 1 exercise, not 3.
     * Used by ProgressService to power the "n out of n completed" dashboard counter.
     */
    private static final String SELECT_COMPLETED_TODAY_SQL =
            "SELECT COUNT(DISTINCT exerciseId) FROM workout_logs " +
                    "WHERE userId = ? AND date = ? AND isCompleted = 1";

    private static final String DELETE_SQL =
            "DELETE FROM workout_logs WHERE logId = ? AND userId = ?";

    public WorkoutLogsDAO() {
        super();
    }

    public ArrayList<WorkoutLog> getLogs(int userId) {
        ArrayList<WorkoutLog> listLogs = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_USER_SQL);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                WorkoutLog newLog = new WorkoutLog(
                        rs.getInt("logId"),
                        rs.getInt("userId"),
                        rs.getInt("exerciseId"),
                        rs.getInt("isCompleted") == 1,
                        rs.getString("date"),
                        rs.getInt("reps"),
                        rs.getDouble("weight")
                );
                listLogs.add(newLog);
            }
            return listLogs;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return null;
        }
    }

    /*
     * getLastLogForExercise
     * Returns the single most recent log entry for a given user and exercise.
     * Returns null if the user has never logged that exercise — this is the signal
     * ProgressService uses to fall back to the starting_weights table instead.
     */
    public WorkoutLog getLastLogForExercise(int userId, int exerciseId) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_LAST_LOG_SQL);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, exerciseId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new WorkoutLog(
                        rs.getInt("logId"),
                        rs.getInt("userId"),
                        rs.getInt("exerciseId"),
                        rs.getInt("isCompleted") == 1,
                        rs.getString("date"),
                        rs.getInt("reps"),
                        rs.getDouble("weight")
                );
            }
            return null;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return null;
        }
    }

    public boolean saveWorkoutLog(WorkoutLog logToSave) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
            pstmt.setInt(1, logToSave.getUserId());
            pstmt.setInt(2, logToSave.getExerciseId());
            pstmt.setInt(3, logToSave.getCompletion());
            pstmt.setString(4, logToSave.getDate());
            pstmt.setInt(5, logToSave.getReps());
            pstmt.setDouble(6, logToSave.getWeight());
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteWorkoutLog(int logId, int userId) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, logId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteWorkoutLog(WorkoutLog log) {
        return deleteWorkoutLog(log.getLogId(), log.getUserId());
    }

    /*
     * getCompletedExercisesCountToday
     * Returns the number of distinct exercises the user has marked completed today.
     * This is the "n completed" half of the dashboard progress counter.
     * The "n total" comes from the user's fitness profile (minsAvailablePerWorkout / 10).
     *
     * @param userId the logged-in user's ID
     * @return count of distinct exercises completed today, 0 if none or on error
     */
    public int getCompletedExercisesCountToday(int userId) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            // today's date in yyyy-MM-dd format — matches how WorkoutLog.date is stored
            String today = LocalDate.now().toString();

            PreparedStatement pstmt = connection.prepareStatement(SELECT_COMPLETED_TODAY_SQL);
            pstmt.setInt(1, userId);
            pstmt.setString(2, today);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return 0;
        }
    }

    /*
     * getLoggedDatesThisWeek
     * Returns every date in the current Mon-Sun week on which the user logged at
     * least one completed set. The dashboard uses this to:
     * - Show "X / Y workouts" (size of returned set vs planned days count)
     * - Mark day cards as "Completed" when their date is in the set
     *
     * @param userId the logged-in user's ID
     * @return set of LocalDate values for each workout day completed this week
     */
    public Set<LocalDate> getLoggedDatesThisWeek(int userId) {
        Set<LocalDate> dates = new HashSet<>();
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(DayOfWeek.MONDAY);
            LocalDate sunday = today.with(DayOfWeek.SUNDAY);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_WEEK_DATES_SQL);
            pstmt.setInt(1, userId);
            pstmt.setString(2, monday.toString());
            pstmt.setString(3, sunday.toString());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                try {
                    dates.add(LocalDate.parse(rs.getString("date")));
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
        }
        return dates;
    }
}
