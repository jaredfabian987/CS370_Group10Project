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
            "userId TEXT NOT NULL," +
            "desc TEXT NOT NULL," +
            "isCompleted INTEGER NOT NULL"+
            ")";

    private static final String INSERT_SQL =
            "INSERT INTO workout_logs (userId, desc, isCompleted) " +
                    "VALUES (?,?,?)";

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
            PreparedStatement pstmt = conn.prepareStatement(SELECT_SQL);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                WorkoutLog newLog = new WorkoutLog(
                        rs.getInt("logId"),
                        rs.getInt("userId"),
                        rs.getString("desc"),
                        rs.getInt("isCompleted") == 1
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
            pstmt.setString(2, logTosave.getDesc());
            pstmt.setInt(3, logTosave.getCompletion());
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
