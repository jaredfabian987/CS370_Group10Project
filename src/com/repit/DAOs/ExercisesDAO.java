package com.repit.DAOs;
import com.repit.Model.Exercise;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;

public class ExercisesDAO extends BaseDAO {
    private static final String SELECT_SQL =
            "SELECT * FROM exercises WHERE userId = ?";

    private static final String TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS exercises (" +
                    "exerciseId INTEGER PRIMARY KEY," +
                    "userId INTEGER NOT NULL," +
                    "name TEXT NOT NULL," +
                    "sets INTEGER NOT NULL,"+
                    "reps INTEGER NOT NULL," +
                    "muscleGroup TEXT NOT NULL,"+
                    "difficulty TEXT NOT NULL"+
                    ")";

    private static final String INSERT_SQL =
            "INSERT INTO exercises (exerciseId, userId, name, sets, reps, muscleGroup, difficulty) "+
                    "VALUES (?,?,?,?,?,?,?)";

    private static final String DELETE_SQL =
            "DELETE FROM exercises WHERE exerciseId = ? AND userId = ?";


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
                Exercise newExercise = new Exercise(
                        rs.getInt("exerciseId"), rs.getInt("userId"),
                        rs.getString("name"), rs.getInt("sets"),
                        rs.getInt("reps"), rs.getString("muscleGroup"),
                        rs.getString("difficulty")
                );
                UserExercises.add(newExercise);
            }
            return UserExercises;
        }  catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean saveExercise(Exercise pExercise){
        try{
            Connection conn = DriverManager.getConnection("jdbc:sqlite:repit.db");
            Statement stmt = conn.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL);
            pstmt.setInt(1,pExercise.getUserId());
            pstmt.setString(2, pExercise.getName());
            pstmt.setInt(3,pExercise.getSets());
            pstmt.setInt(4,pExercise.getReps());
            pstmt.setString(5, pExercise.getMuscleGroup());
            pstmt.setString(6, pExercise.getDifficulty());
            pstmt.executeUpdate();
            return true;

        } catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteExercise(int exerciseId, int userId){
        try{
            Connection conn = DriverManager.getConnection("jdbc:sqlite:repit.db");
            Statement stmt = conn.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, exerciseId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;
        } catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteExercise(Exercise pExercise){
        int exerciseId = pExercise.getId();
        int userId = pExercise.getUserId();
        return deleteExercise(exerciseId, userId);
    }
}
