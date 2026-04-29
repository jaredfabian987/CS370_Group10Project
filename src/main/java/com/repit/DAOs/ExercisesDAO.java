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
    // userId = 0 is reserved for global/seeded exercises visible to everyone
    // userId = -1 is the default on the Exercise model (no owner)
    // this query returns both the user's custom exercises AND the global library
    private static final String SELECT_SQL =
            "SELECT * FROM exercises WHERE userId = ? OR userId = 0";

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
                    "userId INTEGER,"+
                    // short coaching tip shown on the workout screen
                    // defaults to "Target reps: 6-10" for any exercise that doesn't
                    // have a specific cue seeded — updated per-exercise in seedCoachingCues()
                    "coachingCue TEXT NOT NULL DEFAULT 'Target reps: 6-10'"+
                    ")";

    private static final String INSERT_SQL =
            "INSERT INTO exercises (exerciseId, name, category, difficulty, exerciseType, compoundScore, " +
                    "trackingType, isCustom, userId) "+
                    "VALUES (?,?,?,?,?,?,?,?,?)";

    private static final String DELETE_SQL =
            "DELETE FROM exercises WHERE exerciseId = ?";

    TargetedMusclesDAO targetedMusclesDAO = new TargetedMusclesDAO();
    EquipmentDAO equipmentDAO = new EquipmentDAO();

    /*
     * migrateCoachingCue
     * Adds the coachingCue column to existing exercises tables that were created
     * before this field was introduced. SQLite throws if the column already exists
     * so we catch and ignore that case — the table is already up to date.
     */
    private void migrateCoachingCue() {
        try {
            connection.createStatement().execute(
                    "ALTER TABLE exercises ADD COLUMN coachingCue TEXT NOT NULL DEFAULT 'Target reps: 6-10'");
        } catch (SQLException ignored) {}
    }

    /*
     * seedCoachingCues
     * Updates exercises that have a specific coaching cue beyond the default.
     * Called once on startup alongside seedTables() — safe to call repeatedly
     * because it only overwrites exercises that exist by name.
     *
     * Adding a new cue: just add another UPDATE line here following the same pattern.
     * The value will be applied on the next app launch.
     */
    public void seedCoachingCues() {
        String[][] cues = {
                // compound lower body
                {"Romanian Deadlift", "Stay braced through the hinge and keep the bar close to your legs"},
                {"Barbell Deadlift", "Stay braced through the hinge and keep the bar close to your legs"},
                {"Barbell Squat", "Brace your core, keep chest up, and drive through your heels"},
                {"Leg Press", "Keep your back flat against the pad and drive through your heels"},
                // compound upper body push
                {"Bench Press", "Arch your back, retract your shoulder blades, and drive through your legs"},
                {"Overhead Press", "Brace your core and squeeze your glutes to protect your lower back"},
                {"Incline Bench Press", "Control the descent and touch the upper chest, not the neck"},
                // compound upper body pull
                {"Pull Up", "Lead with your elbows and think about pulling them down to your hips"},
                {"Barbell Row", "Keep your back flat and pull the bar to your lower chest"},
                {"Lat Pulldown", "Lean back slightly and pull the bar to your upper chest"},
                // isolation
                {"Tricep Pushdown", "Keep your elbows pinned at your sides throughout the movement"},
                {"Bicep Curl", "Avoid swinging — let the bicep do the work on every rep"},
                {"Lateral Raise", "Lead with your elbows, not your wrists, and stop at shoulder height"},
                {"Leg Curl", "Control the weight on the way up and squeeze the hamstring at the top"},
                {"Leg Extension", "Squeeze the quad at full extension and lower slowly"},
        };

        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            for (String[] pair : cues) {
                PreparedStatement pstmt = connection.prepareStatement(
                        "UPDATE exercises SET coachingCue = ? WHERE name = ?");
                pstmt.setString(1, pair[1]);
                pstmt.setString(2, pair[0]);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("seedCoachingCues error: " + e.getMessage());
        }
    }

    public ArrayList<Exercise> getExercises(int userId){
        ArrayList<Exercise> UserExercises = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:repit.db");
            Statement stmt = conn.createStatement();
            stmt.execute(TABLE_SQL);
            migrateCoachingCue();

            PreparedStatement pstmt = conn.prepareStatement(SELECT_SQL);
            pstmt.setInt(1, userId);
            //pstmt.executeUpdate();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int exerciseId = rs.getInt("exerciseId");
                List<TargetedMuscle> primaryMuscles = targetedMusclesDAO.getTargetedMusclesFromExerciseId(exerciseId, MuscleRole.PRIMARY);
                List<TargetedMuscle> secondaryMuscles = targetedMusclesDAO.getTargetedMusclesFromExerciseId(exerciseId, MuscleRole.SECONDARY);
                List<Equipment> requiredEquipment = equipmentDAO.getEquipmentsFromExercise(exerciseId);
                MuscleGroup muscle = MuscleGroup.values()[rs.getInt("category")];
                DifficultyLevel difficultyLevel = DifficultyLevel.values()[rs.getInt("difficulty")];
                ExerciseType exerciseType = ExerciseType.values()[rs.getInt("exerciseType")];
                TrackingType trackingType = TrackingType.values()[rs.getInt("trackingType")];
                Exercise newExercise = new Exercise(
                        exerciseId,
                        rs.getString("name"),
                        muscle,
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
                newExercise.setCoachingCue(rs.getString("coachingCue"));
                // isCompound is not stored as its own column — derive it from exerciseType
                // COMPOUND and STRENGTH both go into the compound heap in ExercisePriorityQueue
                newExercise.setCompound(
                        exerciseType == ExerciseType.COMPOUND || exerciseType == ExerciseType.STRENGTH);
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
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
            pstmt.setInt(1, exercise.getExerciseId());
            pstmt.setString(2, exercise.getName());
            pstmt.setInt(3, exercise.getMuscleOrdinal());
            pstmt.setInt(4, exercise.getDifficultyOrdinal());
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