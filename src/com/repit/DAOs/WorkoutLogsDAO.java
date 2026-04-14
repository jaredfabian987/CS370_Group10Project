package com.repit.DAOs;
import com.repit.Model.WorkoutLog;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;

public class WorkoutLogsDAO {
    private int userId;

    private static final String TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS workout_logs (" +
            "logId INTEGER PRIMARY KEY," +
            "userId INTEGER NOT NULL," +
            "exerciseId INTEGER NOT NULL," +
            "isCompleted INTEGER NOT NULL,"+
            "date TEXT NOT NULL"+
            ")";

    private static final String INSERT_SQL =
            "INSERT INTO workout_logs (userId, exerciseId, isCompleted, date) " +
                    "VALUES (?,?,?,?)";

    private static final String SELECT_SQL =
            "SELECT * FROM workout_logs WHERE userId = ?";

    private static final String DELETE_SQL =
            "DELETE FROM workout_logs WHERE logId = ? AND userID = ?";

    WorkoutLogsDAO(int newId){
        userId = newId;
    }

    public ArrayList<WorkoutLog> getLogs(){
        ArrayList<WorkoutLog> listLogs = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:repit.db");
            Statement stmt = connect.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = conn.prepareStatement(SELECT_SQL);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                WorkoutLog newLog = new WorkoutLog(
                        rs.getInt("logId"),
                        rs.getInt("userId"),
                        rs.getInt("exerciseId"),
                        rs.getInt("isCompleted") == 1,
                        rs.getString("date")
                );
                listLogs.add(newLog);
            }
            return listLogs;
        } catch (Exception e){
            System.out.println("Database Error: "+ e.getMessage());
            return null;
        }
    }

    public boolean saveWorkoutLog(WorkoutLog logTosave){
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:repit.db");
            Statement stmt = conn.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL);
            pstmt.setInt(1, logTosave.getUserId());
            pstmt.setInt(2, logTosave.getExerciseId());
            pstmt.setInt(3, logTosave.getCompletion());
            pstmt.setString(4, logTosave.getDate());
            pstmt.executeUpdate();
            return true;

        } catch (Exception e){
            System.out.println("Database Error: "+ e.getMessage());
            return false;
        }
    }

    public boolean deleteWorkoutLog(int logId){
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:repit.db");
            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, logId);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e){
            System.out.println("Database Error: "+ e.getMessage());
            return false;
        }
    }

    public boolean deleteWorkoutLog(WorkoutLog log){
        int logId = log.getLogId();
        return deleteWorkoutLog(logId);
    }
}
