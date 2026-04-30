package com.repit.DAOs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * DatabaseSeeder
 * Populates the exercises table with the 23 exercises that have videos in the app.
 *
 * Global exercises use userId = 0 (reserved — not a real user).
 * ExercisesDAO SELECT_SQL includes "OR userId = 0" so every logged-in user
 * automatically gets this library without any extra steps.
 *
 * Idempotent: uses INSERT OR IGNORE so calling seed() on every launch is safe —
 * existing rows are silently skipped and nothing gets duplicated.
 *
 * Enum ordinals:
 *   MuscleGroup  : CHEST=0, BACK=1, SHOULDERS=2, BICEPS=3, TRICEPS=4,
 *                  QUADS=5, HAMSTRINGS=6, GLUTES=7, CALVES=8, CARDIO=9, FULL_BODY=10
 *   DifficultyLevel : BEGINNER=0, INTERMEDIATE=1, ADVANCED=2
 *   ExerciseType : COMPOUND=0, ISOLATION=1, STRENGTH=2, CARDIO=3, FLEXIBILITY=4
 *   TrackingType : REPS_AND_WEIGHT=0, REPS_ONLY=1, TIME=2, DISTANCE=3
 *
 * To add a new exercise: add a row to the EXERCISES array with a unique exerciseId.
 * The name must exactly match the video file name used in the workout screen.
 *
 * @author claude sonnet 4.6
 * given prompt could you please make a file to seed my database with the following exercises for which exercises
 * I have videos for given this list of videos:
 * [Exercises On Rep-It] (23 exercises)
 *
 * Arm workouts:
 * cable bicep curls
 * cable hammer curls
 * inclined bicep curls
 * over head tricep extensions
 * tricep push downs
 *
 * Back workouts:
 * cable rows
 * lat pull downs (wide grip)
 * lat pull downs (close grip)
 * smith machine row
 *
 * Chest workouts:
 * cable chest flys (lower chest)
 * cable chest flys (upper chest)
 * flat bench (barbell bench press)
 * incline smith machine bench press
 * flat bench (dumbbell bench press)
 * incline bench (dumbbell bench press)
 *
 * Leg workouts:
 * dumbbell RDLs
 * leg extensions
 * squat free weight
 * leg curls laying down
 * seated calf raises
 *
 * Shoulder workouts:
 * dumbbell shoulder press
 * seated dumbbell lateral raises
 * seated rear delt flys
 *
 */
public class DatabaseSeeder {

    private static final String DB_URL = "jdbc:sqlite:repit.db";

    // columns: exerciseId, name, category, exerciseType, compoundScore, trackingType, isCustom, userId
    // difficulty is not used — all exercises default to 0 in the DB
    // isCustom = 0 (false), userId = 0 (global library)
    // exerciseType: COMPOUND=0, ISOLATION=1
    // compoundScore: 1-10, higher = more important compound movement
    private static final Object[][] EXERCISES = {

        // ARM WORKOUTS
        // Biceps (3) — all isolation (1)
        { 1,  "Cable Bicep Curls",           3, 1, 3, 0, 0, 0 },
        { 2,  "Cable Hammer Curls",           3, 1, 3, 0, 0, 0 },
        { 3,  "Inclined Bicep Curls",         3, 1, 3, 0, 0, 0 },
        // Triceps (4) — all isolation (1)
        { 4,  "Overhead Tricep Extensions",   4, 1, 3, 0, 0, 0 },
        { 5,  "Tricep Pushdowns",             4, 1, 3, 0, 0, 0 },

        // BACK WORKOUTS
        // Back (1) — all compound (0)
        { 6,  "Cable Rows",                  1, 0, 7, 0, 0, 0 },
        { 7,  "Lat Pulldowns (Wide Grip)",   1, 0, 7, 0, 0, 0 },
        { 8,  "Lat Pulldowns (Close Grip)",  1, 0, 7, 0, 0, 0 },
        { 9,  "Smith Machine Row",           1, 0, 8, 0, 0, 0 },

        // CHEST WORKOUTS
        // Chest (0) — isolations (1)
        { 10, "Cable Chest Flys (Lower Chest)",       0, 1, 3, 0, 0, 0 },
        { 11, "Cable Chest Flys (Upper Chest)",       0, 1, 3, 0, 0, 0 },
        // Chest (0) — compounds (0)
        { 12, "Flat Bench (Barbell Bench Press)",     0, 0, 9, 0, 0, 0 },
        { 13, "Incline Smith Machine Bench Press",    0, 0, 8, 0, 0, 0 },
        { 14, "Flat Bench (Dumbbell Bench Press)",    0, 0, 8, 0, 0, 0 },
        { 15, "Incline Bench (Dumbbell Bench Press)", 0, 0, 7, 0, 0, 0 },

        // LEG WORKOUTS
        // compounds (0)
        { 16, "Dumbbell RDLs",              6, 0, 8,  0, 0, 0 }, // Hamstrings (6)
        { 17, "Squat Free Weight",          5, 0, 10, 0, 0, 0 }, // Quads (5)
        // isolations (1)
        { 18, "Leg Extensions",            5, 1, 3, 0, 0, 0 },  // Quads (5)
        { 19, "Leg Curls Laying Down",     6, 1, 3, 0, 0, 0 },  // Hamstrings (6)
        { 20, "Seated Calf Raises",        8, 1, 2, 0, 0, 0 },  // Calves (8)

        //  SHOULDER WORKOUTS
        // Shoulders (2) — compound (0)
        { 21, "Dumbbell Shoulder Press",        2, 0, 8, 0, 0, 0 },
        // Shoulders (2) — isolations (1)
        { 22, "Seated Dumbbell Lateral Raises", 2, 1, 3, 0, 0, 0 },
        { 23, "Seated Rear Delt Flys",          2, 1, 3, 0, 0, 0 },
    };

    // Secondary muscles for each exercise — one per exercise (schema UNIQUE constraint on exerciseId).
    // Primary muscle is already in the exercises.category column, so this table is secondary-only.
    // muscle text must match MuscleGroup.name() exactly (case-insensitive) for scoring to work.
    // role = 1 (SECONDARY in MuscleRole enum).
    // Format: { exerciseId, secondaryMuscle }
    // Only exercises where a secondary muscle meaningfully boosts their workout-day score are listed.
    private static final Object[][] SECONDARY_MUSCLES = {
        // Back exercises → BICEPS secondary (+1 on pull day)
        { 6,  "BICEPS" }, // Cable Rows
        { 7,  "BICEPS" }, // Lat Pulldowns (Wide Grip)
        { 8,  "BICEPS" }, // Lat Pulldowns (Close Grip)
        { 9,  "BICEPS" }, // Smith Machine Row

        // Chest compounds → TRICEPS secondary (+1 on push day)
        { 12, "TRICEPS" }, // Flat Bench (Barbell Bench Press)
        { 13, "TRICEPS" }, // Incline Smith Machine Bench Press
        { 14, "TRICEPS" }, // Flat Bench (Dumbbell Bench Press)
        { 15, "SHOULDERS" }, // Incline Bench (Dumbbell Bench Press) — hits front delt

        // Leg compounds → GLUTES secondary (+1 on legs day)
        { 16, "GLUTES" }, // Dumbbell RDLs
        { 17, "GLUTES" }, // Squat Free Weight

        // Shoulder press → TRICEPS secondary (+1 on push day)
        { 21, "TRICEPS" }, // Dumbbell Shoulder Press
    };

    /**
     * Seeds the 23 exercises and their secondary muscles into the database.
     * Safe to call on every app launch — INSERT OR IGNORE skips rows that already exist.
     */
    public static void seed() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            // ensure the exercises table exists
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS exercises (" +
                "exerciseId INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "category INTEGER NOT NULL," +
                "difficulty INTEGER NOT NULL," +
                "exerciseType INTEGER NOT NULL," +
                "compoundScore INTEGER NOT NULL," +
                "primaryMusclesId INTEGER," +
                "secondaryMusclesId INTEGER," +
                "requiredEquipmentId INTEGER," +
                "trackingType INTEGER NOT NULL," +
                "isCustom TEXT NOT NULL," +
                "userId INTEGER," +
                "coachingCue TEXT NOT NULL DEFAULT 'Target reps: 6-10'" +
                ")"
            );

            String insertSql =
                "INSERT OR IGNORE INTO exercises " +
                "(exerciseId, name, category, difficulty, exerciseType, compoundScore, " +
                "primaryMusclesId, secondaryMusclesId, requiredEquipmentId, " +
                "trackingType, isCustom, userId) " +
                "VALUES (?,?,?,?,?,?,NULL,NULL,NULL,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(insertSql);

            for (Object[] ex : EXERCISES) {
                pstmt.setInt(1, (int) ex[0]); // exerciseId
                pstmt.setString(2,(String) ex[1]); // name
                pstmt.setInt(3, (int) ex[2]); // category (MuscleGroup ordinal)
                pstmt.setInt(4,0); // difficulty — not used, always 0
                pstmt.setInt(5, (int) ex[3]); // exerciseType (COMPOUND=0, ISOLATION=1)
                pstmt.setInt(6, (int) ex[4]); // compoundScore
                pstmt.setInt(7, (int) ex[5]); // trackingType
                pstmt.setInt(8, (int) ex[6]); // isCustom (0 = false)
                pstmt.setInt(9, (int) ex[7]); // userId (0 = global)
                pstmt.executeUpdate();
            }

            // seed secondary muscles into the muscles table
            // the muscles table has a UNIQUE constraint on exerciseId so only one row per exercise
            // primary muscle is already in exercises.category — this table is secondary-only
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS muscles (" +
                "targetedMuscleId INTEGER PRIMARY KEY," +
                "exerciseId INTEGER NOT NULL UNIQUE," +
                "muscle TEXT NOT NULL," +
                "role INTEGER NOT NULL" +
                ")"
            );

            String muscleInsertSql =
                "INSERT OR IGNORE INTO muscles (exerciseId, muscle, role) VALUES (?, ?, 1)";
            PreparedStatement musclePstmt = conn.prepareStatement(muscleInsertSql);

            for (Object[] sm : SECONDARY_MUSCLES) {
                musclePstmt.setInt(1,    (int) sm[0]);    // exerciseId
                musclePstmt.setString(2, (String) sm[1]); // muscle name (must match MuscleGroup.name())
                musclePstmt.executeUpdate();
            }

            System.out.println("DatabaseSeeder: 23 exercises seeded successfully.");

        } catch (Exception e) {
            System.out.println("DatabaseSeeder error: " + e.getMessage());
        }
    }
}
