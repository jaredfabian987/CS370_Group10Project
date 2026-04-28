package com.repit.DAOs;
import com.repit.Model.Exercise;
import com.repit.Model.FitnessProfile;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
/*this.weight = weight;
        this.height = height;
        this.daysPerWeek = daysPerWeek;
        this.minsAvailablePerWorkout = minsAvailablePerWorkout;
        this.level = level;
        this.goal = goal;*/
public class FitnessProfileDAO extends BaseDAO{
    private static final String SELECT_SQL =
            "SELECT * FROM profiles WHERE userId = ?";

    private static final String TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS profiles (" +
                    "profileId INTEGER PRIMARY KEY," +
                    "userId INTEGER NOT NULL UNIQUE," +
                    "weight REAL NOT NULL," +
                    "height REAL NOT NULL,"+
                    "daysPerWeek INTEGER NOT NULL," +
                    "minsAvailablePerWorkout INTEGER NOT NULL,"+
                    "level INTEGER NOT NULL,"+
                    "goal INTEGER NOT NULL"+
                    ")";

    private static final String INSERT_SQL =
            "INSERT INTO profiles (userId, weight, height, daysPerWeek, minsAvailablePerWorkout, level, goal) "+
                    "VALUES (?,?,?,?,?,?,?)";

    private static final String DELETE_SQL =
            "DELETE FROM profiles WHERE exerciseId = ? AND userId = ?";

    private static final String UPDATE_SQL =
            "UPDATE profiles SET weight=?, height=?, daysPerWeek=?, minsAvailablePerWorkout=?, level=?, goal=? WHERE userId=?";

    public boolean saveProfile(FitnessProfile profile){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
            pstmt.setInt(1, profile.getUserId());
            pstmt.setDouble(2, profile.getWeight());
            pstmt.setDouble(3, profile.getHeight());
            pstmt.setInt(4, profile.getDaysPerWeek());
            pstmt.setDouble(5, profile.getMinsAvailablePerWorkout());
            pstmt.setInt(6, profile.getLevelByInt());
            pstmt.setInt(7, profile.getGoalByInt());
            pstmt.executeUpdate();
            return true;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public FitnessProfile getProfile(int userId){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_SQL);
            pstmt.setInt(1, userId);
            //pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return new FitnessProfile(
                        rs.getInt("profileId"),
                        rs.getInt("userId"),
                        rs.getDouble("weight"),
                        rs.getDouble("height"),
                        rs.getInt("daysPerWeek"),
                        rs.getDouble("minsAvailablePerWorkout"),
                        rs.getInt("level"),
                        rs.getInt("goal")
                );
            }

        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    public boolean updateProfile(FitnessProfile profile){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(UPDATE_SQL);
            pstmt.setInt(1, profile.getUserId());
            pstmt.setDouble(2, profile.getWeight());
            pstmt.setDouble(3, profile.getHeight());
            pstmt.setInt(4, profile.getDaysPerWeek());
            pstmt.setDouble(5, profile.getMinsAvailablePerWorkout());
            pstmt.setInt(6, profile.getLevelByInt());
            pstmt.setInt(7, profile.getGoalByInt());
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean deleteProfile(int profileId){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, profileId);
            pstmt.executeUpdate();
            return true;
        } catch(Exception e) {
            System.out.println("FitnessProfile Database Error: "+ e.getMessage());
            return false;
        }
    }

}
