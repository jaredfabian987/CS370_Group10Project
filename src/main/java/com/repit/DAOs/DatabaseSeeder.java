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

    /*
     * STARTING_WEIGHTS — week-1 baselines (in lbs) for users who have never logged
     * a set yet. Indexed by exerciseId, with three columns: BEG, INT, ADV (matching
     * FitnessLevel.values() ordinals).
     *
     * Numbers are based on average gym benchmarks (Stronglifts/Strength Standards-style)
     * for an average ~165 lb adult. Once a user logs an actual working set,
     * ProgressService uses that real weight instead — these only matter on day 1.
     *
     * Dumbbell entries are PER DUMBBELL (each hand). Cable/machine entries are
     * total stack weight. No entries here for bodyweight movements.
     *
     * Format: { exerciseId, BEG_lbs, INT_lbs, ADV_lbs }
     */
    private static final Object[][] STARTING_WEIGHTS = {
        // Biceps isolations
        { 1,  20.0, 35.0, 50.0 }, // Cable Bicep Curls
        { 2,  20.0, 35.0, 50.0 }, // Cable Hammer Curls
        { 3,  15.0, 25.0, 40.0 }, // Inclined Bicep Curls (per dumbbell)

        // Triceps isolations
        { 4,  25.0, 45.0, 70.0 }, // Overhead Tricep Extensions
        { 5,  30.0, 55.0, 85.0 }, // Tricep Pushdowns

        // Back compounds
        { 6,  60.0, 110.0, 170.0 }, // Cable Rows
        { 7,  60.0, 110.0, 160.0 }, // Lat Pulldowns (Wide Grip)
        { 8,  60.0, 110.0, 160.0 }, // Lat Pulldowns (Close Grip)
        { 9,  65.0, 115.0, 175.0 }, // Smith Machine Row

        // Chest isolations (cables)
        { 10, 20.0, 35.0, 55.0 }, // Cable Chest Flys (Lower Chest)
        { 11, 20.0, 35.0, 55.0 }, // Cable Chest Flys (Upper Chest)

        // Chest compounds
        { 12, 65.0, 135.0, 205.0 }, // Flat Bench (Barbell Bench Press)
        { 13, 65.0, 115.0, 175.0 }, // Incline Smith Machine Bench Press
        { 14, 30.0,  55.0,  85.0 }, // Flat Bench (Dumbbell Bench Press) — per dumbbell
        { 15, 25.0,  50.0,  75.0 }, // Incline Bench (Dumbbell Bench Press) — per dumbbell

        // Leg compounds
        { 16, 30.0,  55.0,  85.0 }, // Dumbbell RDLs (per dumbbell)
        { 17, 65.0, 155.0, 245.0 }, // Squat Free Weight (barbell, includes the bar)

        // Leg isolations
        { 18, 50.0,  90.0, 140.0 }, // Leg Extensions
        { 19, 40.0,  75.0, 120.0 }, // Leg Curls Laying Down
        { 20, 45.0,  90.0, 145.0 }, // Seated Calf Raises

        // Shoulder compound
        { 21, 25.0,  45.0,  70.0 }, // Dumbbell Shoulder Press (per dumbbell)

        // Shoulder isolations
        { 22, 10.0,  20.0,  30.0 }, // Seated Dumbbell Lateral Raises (per dumbbell)
        { 23, 10.0,  20.0,  30.0 }, // Seated Rear Delt Flys (per dumbbell)
    };

    /*
     * EQUIPMENT — required gear for each of the 23 exercises.
     * Workout screen shows up to 3 entries (reqEquip1/2/3Label), so most
     * exercises here have 1-2 entries.
     *
     * EquipmentType ordinals: FREE_WEIGHT=0, MACHINE=1, CARDIO=2, BODYWEIGHT=3
     * Format: { exerciseId, name, equipmentType }
     */
    private static final Object[][] EQUIPMENT = {
        // Biceps isolations
        { 1,  "Cable Machine",          1 }, // Cable Bicep Curls
        { 2,  "Cable Machine",          1 }, // Cable Hammer Curls
        { 3,  "Dumbbells",              0 }, // Inclined Bicep Curls
        { 3,  "Incline Bench",          0 },

        // Triceps isolations
        { 4,  "Dumbbell",               0 }, // Overhead Tricep Extensions
        { 5,  "Cable Machine",          1 }, // Tricep Pushdowns

        // Back compounds
        { 6,  "Cable Machine",          1 }, // Cable Rows
        { 7,  "Lat Pulldown Machine",   1 }, // Lat Pulldowns (Wide Grip)
        { 8,  "Lat Pulldown Machine",   1 }, // Lat Pulldowns (Close Grip)
        { 9,  "Smith Machine",          1 }, // Smith Machine Row

        // Chest isolations (cables)
        { 10, "Cable Machine",          1 }, // Cable Chest Flys (Lower Chest)
        { 11, "Cable Machine",          1 }, // Cable Chest Flys (Upper Chest)

        // Chest compounds
        { 12, "Barbell",                0 }, // Flat Bench (Barbell Bench Press)
        { 12, "Flat Bench",             0 },
        { 13, "Smith Machine",          1 }, // Incline Smith Machine Bench Press
        { 13, "Incline Bench",          0 },
        { 14, "Dumbbells",              0 }, // Flat Bench (Dumbbell Bench Press)
        { 14, "Flat Bench",             0 },
        { 15, "Dumbbells",              0 }, // Incline Bench (Dumbbell Bench Press)
        { 15, "Incline Bench",          0 },

        // Leg compounds
        { 16, "Dumbbells",              0 }, // Dumbbell RDLs
        { 17, "Barbell",                0 }, // Squat Free Weight
        { 17, "Squat Rack",             0 },

        // Leg isolations
        { 18, "Leg Extension Machine",  1 }, // Leg Extensions
        { 19, "Leg Curl Machine",       1 }, // Leg Curls Laying Down
        { 20, "Calf Raise Machine",     1 }, // Seated Calf Raises

        // Shoulder compound
        { 21, "Dumbbells",              0 }, // Dumbbell Shoulder Press

        // Shoulder isolations
        { 22, "Dumbbells",              0 }, // Seated Dumbbell Lateral Raises
        { 23, "Dumbbells",              0 }, // Seated Rear Delt Flys
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
            // exerciseId is NOT unique — each exercise can have multiple secondary
            // muscles (and we may add more rows in the future)
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS muscles (" +
                "targetedMuscleId INTEGER PRIMARY KEY," +
                "exerciseId INTEGER NOT NULL," +
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

            // seed required equipment for each exercise.
            // ensure the table exists with the schema EquipmentDAO uses
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS equipment (" +
                "equipmentId INTEGER PRIMARY KEY," +
                "exerciseId INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "EquipmentType INTEGER NOT NULL," +
                "isCustom INTEGER NOT NULL" +
                ")"
            );

            // guard against duplicates on every app launch — equipmentId is
            // auto-increment so a plain INSERT would re-insert the whole list each run.
            // we only seed if NO global equipment rows exist yet (custom user
            // equipment is never touched: it has isCustom = 1).
            java.sql.ResultSet eqCountRs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM equipment WHERE isCustom = 0");
            int existingGlobalEquipment = eqCountRs.next() ? eqCountRs.getInt(1) : 0;
            eqCountRs.close();

            if (existingGlobalEquipment == 0) {
                String equipmentInsertSql =
                    "INSERT INTO equipment (exerciseId, name, EquipmentType, isCustom) " +
                    "VALUES (?, ?, ?, 0)";
                PreparedStatement eqPstmt = conn.prepareStatement(equipmentInsertSql);

                for (Object[] eq : EQUIPMENT) {
                    eqPstmt.setInt(1,    (int) eq[0]);    // exerciseId
                    eqPstmt.setString(2, (String) eq[1]); // name
                    eqPstmt.setInt(3,    (int) eq[2]);    // EquipmentType ordinal
                    eqPstmt.executeUpdate();
                }
            }

            // seed week-1 starting weights — keyed by (exerciseId, level).
            // ProgressService falls back to this table when the user has never
            // logged a set for the exercise yet.
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS starting_weights (" +
                "exerciseId INTEGER NOT NULL," +
                "level INTEGER NOT NULL," +
                "weight REAL NOT NULL," +
                "PRIMARY KEY (exerciseId, level)" +
                ")"
            );

            String startingWeightInsertSql =
                "INSERT OR IGNORE INTO starting_weights (exerciseId, level, weight) VALUES (?, ?, ?)";
            PreparedStatement swPstmt = conn.prepareStatement(startingWeightInsertSql);

            for (Object[] row : STARTING_WEIGHTS) {
                int exerciseId = (int) row[0];
                // columns 1..3 are BEG/INT/ADV in FitnessLevel ordinal order
                for (int level = 0; level < 3; level++) {
                    swPstmt.setInt(1, exerciseId);
                    swPstmt.setInt(2, level);
                    swPstmt.setDouble(3, (double) row[level + 1]);
                    swPstmt.executeUpdate();
                }
            }

            System.out.println("DatabaseSeeder: 23 exercises seeded successfully.");

        } catch (Exception e) {
            System.out.println("DatabaseSeeder error: " + e.getMessage());
        }
    }
}
