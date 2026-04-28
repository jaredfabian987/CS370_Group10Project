package com.repit.DAOs;
import com.repit.Model.TargetedMuscle;
import com.repit.Model.enums.MuscleRole;
import com.repit.Model.enums.TrackingType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//Stores Primary and secondary muscles
/*
* private int targetedMuscleId;
    private int exerciseId;
    private String muscle;
    private MuscleRole role;
* */
public class TargetedMusclesDAO extends BaseDAO {
    private static final String SELECT_FROM_EXERCISE_SQL =
            "SELECT * FROM muscles WHERE exerciseId = ?";

    private static final String SELECT_SQL =
            "SELECT * FROM muscles WHERE targetedMuscleId = ?";

    private static final String TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS muscles (" +
                    "targetedMuscleId INTEGER PRIMARY KEY," +
                    "exerciseId INTEGER NOT NULL UNIQUE," +
                    "muscle TEXT NOT NULL," +
                    "role INTEGER NOT NULL"+
                    ")";

    private static final String INSERT_SQL =
            "INSERT INTO muscles (exerciseId, muscle, role) "+
                    "VALUES (?,?,?)";

    private static final String DELETE_SQL =
            "DELETE FROM muscles WHERE targetedMuscleId = ?";

    private static final String UPDATE_SQL =
            "UPDATE muscles SET exerciseId=?, muscle=?, role=?";

    public boolean saveTargetedMuscle(TargetedMuscle targetedMuscle){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
            pstmt.setInt(1, targetedMuscle.getExerciseId());
            pstmt.setString(2, targetedMuscle.getMuscle());
            pstmt.setInt(3, targetedMuscle.getRoleByInt());
            pstmt.executeUpdate();
            return true;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean saveTargetedMuscles(List<TargetedMuscle> muscles){
        for(TargetedMuscle m : muscles){
            if (!saveTargetedMuscle(m)){
                return false;
            }
        }
        return  true;
    }

    public TargetedMuscle getTargetedMuscle(int targetedMuscleId){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_SQL);
            pstmt.setInt(1, targetedMuscleId);
            //pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                return new TargetedMuscle(
                        targetedMuscleId,
                        rs.getInt("exerciseId"),
                        rs.getString("muscle"),
                        rs.getInt("role")
                );
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public ArrayList<TargetedMuscle> getTargetedMusclesFromExerciseId(int exerciseId, MuscleRole role){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(SELECT_FROM_EXERCISE_SQL);
            pstmt.setInt(1, exerciseId);
            pstmt.executeUpdate();


            ArrayList<TargetedMuscle> targetedMusclesFromExercise = new ArrayList<>();
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                int dbRole = rs.getInt("role");
                if (role == null || role.ordinal() == dbRole) {
                    TargetedMuscle temp = new TargetedMuscle(
                            rs.getInt("targetedMuscleId"),
                            rs.getInt("exerciseId"),
                            rs.getString("muscle"),
                            dbRole
                            );
                    targetedMusclesFromExercise.add(temp);
                }
            }
            return targetedMusclesFromExercise;
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public ArrayList<TargetedMuscle> getTargetedMusclesFromExerciseId(int exerciseId){
        return this.getTargetedMusclesFromExerciseId(exerciseId, null);
    }

    public List<String> getStringMusclesFromExerciseId(int exerciseId, MuscleRole role){
        List<String> MuscleStrings = new ArrayList<>();
        ArrayList<TargetedMuscle> Muscles = this.getTargetedMusclesFromExerciseId(exerciseId, role);
        if (Muscles == null){ return null;}

        for(TargetedMuscle Muscle : Muscles){
            MuscleStrings.add(Muscle.getMuscle());
        }
        return MuscleStrings;
    }

    public boolean deleteTargetedMuscle(int targetedMuscleId){
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, targetedMuscleId);
            pstmt.executeUpdate();
            return true;

        } catch(Exception e){
            System.out.println("TargetedMusclesDAO: error occurred deleting!");
        }
        return false;
    }
}
