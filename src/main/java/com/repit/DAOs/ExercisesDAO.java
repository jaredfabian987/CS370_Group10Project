package com.repit.DAOs;
import com.repit.Model.Equipment;
import com.repit.Model.Exercise;
import com.repit.Model.TargetedMuscle;
import com.repit.Model.enums.*;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExercisesDAO extends BaseDAO {
    private static final String SELECT_SQL =
            "SELECT * FROM exercises WHERE userId = ?";

    private static final String TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS exercises (" +
                    //id's
                    "exerciseId INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    //enums
                    "category INTEGER NOT NULL, "+
                    "difficulty INTEGER NOT NULL, "+
                    "exerciseType INTEGER NOT NULL, "+
                    //compounds score for heap
                    "compoundScore INTEGER NOT NULL, "+
                    //muscle identification
                    "primaryMusclesId INTEGER NOT NULL, "+
                    "secondaryMusclesId INTEGER, "+
                    //equipment
                    "requiredEquipmentId NOT NULL,"+
                    "trackingType INTEGER NOT NULL," +
                    "isCustom TEXT NOT NULL,"+
                    "userId INTEGER"+
                    ")";

    private static final String INSERT_SQL =
            "INSERT INTO exercises (exerciseId, name, category, difficulty, exerciseType, compoundScore, " +
                    "trackingType, isCustom, userId) "+
                    "VALUES (?,?,?,?,?,?,?,?,?)";

    private static final String DELETE_SQL =
            "DELETE FROM exercises WHERE exerciseId = ?";

    TargetedMusclesDAO targetedMusclesDAO = new TargetedMusclesDAO();
    EquipmentDAO equipmentDAO = new EquipmentDAO();

    public ArrayList<Exercise> getExercises(int userId){
        ArrayList<Exercise> UserExercises = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:repit.db");
            Statement stmt = conn.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = conn.prepareStatement(SELECT_SQL);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int exerciseId = rs.getInt("exerciseId");
                List<TargetedMuscle> primaryMuscles = targetedMusclesDAO.getTargetedMusclesFromExerciseId(exerciseId, MuscleRole.PRIMARY);
                List<TargetedMuscle> secondaryMuscles = targetedMusclesDAO.getTargetedMusclesFromExerciseId(exerciseId, MuscleRole.SECONDARY);
                List<Equipment>requiredEquipment = equipmentDAO.getEquipmentsFromExercise(exerciseId);
                ExerciseCategory category = ExerciseCategory.values()[rs.getInt("category")];
                DifficultyLevel difficultyLevel = DifficultyLevel.values()[rs.getInt("difficulty")];
                ExerciseType exerciseType = ExerciseType.values()[rs.getInt("exerciseType")];
                TrackingType trackingType = TrackingType.values()[rs.getInt("trackingType")];
                Exercise newExercise = new Exercise(
                        exerciseId,
                        rs.getString("name"),
                        category,
                        difficultyLevel,
                        exerciseType,
                        rs.getInt("compoundScore"),
                        primaryMuscles,
                        secondaryMuscles,
                        requiredEquipment,
                        trackingType,
                        rs.getInt("isCustom")==1,
                        rs.getInt("userId")
                );
                UserExercises.add(newExercise);
            }
            return UserExercises;
        }  catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }


    /*
    * "INSERT INTO exercises (exerciseId, name, category, difficulty, exerciseType, compoundScore, " +
                    "primaryMuscles, secondaryMuscles, requiredEquipmentId, trackingType, isCustom, userId) "+
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    * */
    public boolean saveExercise(Exercise exercise){
        try{
            Connection conn = DriverManager.getConnection("jdbc:sqlite:repit.db");
            Statement stmt = conn.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL);
            pstmt.setInt(1, exercise.getExerciseId());
            pstmt.setString(2, exercise.getName());
            pstmt.setInt(3,exercise.getCategoryOrdinal());
            pstmt.setInt(4,exercise.getDifficultyOrdinal());
            pstmt.setInt(5, exercise.getCompoundScore());
            pstmt.setInt(6, exercise.getTrackingTypeOrdinal());
            pstmt.setInt(7, exercise.isCustomOrdinal());
            pstmt.setInt(8, exercise.getUserId());
            pstmt.executeUpdate();

            targetedMusclesDAO.saveTargetedMuscles(exercise.getPrimaryMuscles());
            targetedMusclesDAO.saveTargetedMuscles(exercise.getSecondaryMuscles());
            return true;

        } catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteExercise(int exerciseId){
        try{
            Connection conn = DriverManager.getConnection("jdbc:sqlite:repit.db");
            Statement stmt = conn.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, exerciseId);
            pstmt.executeUpdate();
            return true;
        } catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteExercise(Exercise exercise){
        int exerciseId = exercise.getExerciseId();
        //int userId = exercise.getUserId();
        return deleteExercise(exerciseId);
    }
}
