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
    // category (MuscleGroup ordinal): CHEST=0, BACK=1, SHOULDERS=2, BICEPS=3, TRICEPS=4,
    //                                 QUADS=5, HAMSTRINGS=6, GLUTES=7, CALVES=8, CARDIO=9, FULL_BODY=10
    // exerciseType: COMPOUND=0, ISOLATION=1, STRENGTH=2, CARDIO=3, FLEXIBILITY=4
    // trackingType: REPS_AND_WEIGHT=0, REPS_ONLY=1, TIME=2, DISTANCE=3
    // compoundScore: 1-10, higher = more important compound movement (0 for non-strength)
    //
    // Exercise NAMES intentionally align with the corresponding /Videos/ filenames
    // (de-kebab-cased) so workoutController.VIDEO_MAP can resolve them cleanly.
    private static final Object[][] EXERCISES = {

        // ARM WORKOUTS
        // Biceps (3) — isolations (1)
        { 1,  "Cable Bicep Curl",                3, 1, 3, 0, 0, 0 }, // cable-bicep-curl.mp4
        { 2,  "Cable Hammer Curl",               3, 1, 3, 0, 0, 0 }, // cable-hammer-curl.mp4
        { 3,  "Bicep Curl",                      3, 1, 3, 0, 0, 0 }, // bicep-curl.mp4
        // Triceps (4) — isolations (1)
        { 4,  "Overhead Tricep Extension",       4, 1, 3, 0, 0, 0 }, // overhead-tricep-extension.mp4
        { 5,  "Tricep Pushdown",                 4, 1, 3, 0, 0, 0 }, // tricep-pushdown.mp4

        // BACK WORKOUTS — compounds (0)
        { 6,  "Seated Cable Row",                1, 0, 7, 0, 0, 0 }, // seated-cable-row.mp4
        { 7,  "Lat Pulldown",                    1, 0, 7, 0, 0, 0 }, // lat-pulldown.mp4
        { 8,  "Close Grip Lat Pulldown",         1, 0, 7, 0, 0, 0 }, // close-grip-lat-pulldown.mp4
        { 9,  "Smith Machine Row",               1, 0, 8, 0, 0, 0 }, // barbell-row.mp4 — video shows it on a Smith machine

        // CHEST WORKOUTS
        // isolations (1)
        { 10, "Lower Cable Chest Fly",           0, 1, 3, 0, 0, 0 }, // lower-cable-chest-fly.mp4
        { 11, "Upper Cable Chest Fly",           0, 1, 3, 0, 0, 0 }, // upper-cable-chest-fly.mp4
        // compounds (0)
        { 12, "Bench Press",                     0, 0, 9, 0, 0, 0 }, // bench-press.mp4
        { 13, "Smith Machine Incline Bench Press", 0, 0, 8, 0, 0, 0 }, // smith-machine-incline-bench-press.mp4
        { 14, "Dumbbell Bench Press",            0, 0, 8, 0, 0, 0 }, // dumbbell-bench-press.mp4
        { 15, "Incline Bench Press",             0, 0, 7, 0, 0, 0 }, // incline-bench-press.mp4

        // LEG WORKOUTS
        // compounds (0)
        { 16, "Romanian Deadlift",               6, 0, 8,  0, 0, 0 }, // romanian-deadlift.mp4 — Hamstrings (6)
        { 17, "Barbell Squat",                   5, 0, 10, 0, 0, 0 }, // barbell-squat.mp4     — Quads (5)
        // isolations (1)
        { 18, "Leg Extension",                   5, 1, 3, 0, 0, 0 }, // leg-extension.mp4     — Quads (5)
        { 19, "Leg Curl",                        6, 1, 3, 0, 0, 0 }, // leg-curl.mp4          — Hamstrings (6)
        { 20, "Seated Calf Raise",               8, 1, 2, 0, 0, 0 }, // seated-calf-raise.mp4 — Calves (8)

        // SHOULDER + CHEST FLY
        // Shoulders (2) — compound (0)
        { 21, "Overhead Press",                  2, 0, 8, 0, 0, 0 }, // overhead-press.mp4
        // Chest (0) — isolation (1)  (was "Lateral Raises" — replaced because no lateral-raise.mp4 exists)
        { 22, "Dumbbell Chest Fly",              0, 1, 3, 0, 0, 0 }, // dumbbell-chest-fly.mp4
        // Shoulders (2) — isolation (1)
        { 23, "Rear Delt Fly",                   2, 1, 3, 0, 0, 0 }, // rear-delt-fly.mp4

        // BODYWEIGHT — strength (compounds + core)
        // suggestedWeight stays 0 so workoutController shows the bodyweight UI branch
        // (em dash for warmups, target reps for working sets).
        { 24, "Pull Up",                         1,  0, 8, 1, 0, 0 }, // pullups.mp4       — Back (1) compound, REPS_ONLY (1)
        { 25, "Push Up",                         0,  0, 6, 1, 0, 0 }, // pushups.mp4       — Chest (0) compound, REPS_ONLY (1)
        { 26, "Sit Up",                         10,  1, 2, 1, 0, 0 }, // situps.mp4        — Full Body (10) core isolation
        { 27, "Russian Twist",                  10,  1, 2, 1, 0, 0 }, // russian-twists.mp4 — Full Body (10) core isolation
        { 28, "Leg Lift",                       10,  1, 2, 1, 0, 0 }, // leglifts.mp4      — Full Body (10) core isolation

        // CARDIO — single-activity, time-based (TrackingType.TIME = 2, ExerciseType.CARDIO = 3)
        // PlannerService picks ONE of these for a CARDIO day.
        // TODO (workoutController): when currentExercise.getExerciseType() == ExerciseType.CARDIO,
        //   replace the "Log Sets" panel with a session timer that records elapsed time
        //   instead of reps/weight. Save as a single WorkoutLog with reps = elapsedSeconds.
        { 29, "Stairmaster",                     9,  3, 0, 2, 0, 0 }, // stairmaster.mp4
        { 30, "Treadmill",                       9,  3, 0, 2, 0, 0 }, // treadmill.mp4
    };

    // Secondary muscles for each exercise — one per exercise (schema UNIQUE constraint on exerciseId).
    // Primary muscle is already in the exercises.category column, so this table is secondary-only.
    // muscle text must match MuscleGroup.name() exactly (case-insensitive) for scoring to work.
    // role = 1 (SECONDARY in MuscleRole enum).
    // Format: { exerciseId, secondaryMuscle }
    // Only exercises where a secondary muscle meaningfully boosts their workout-day score are listed.
    private static final Object[][] SECONDARY_MUSCLES = {
        // Back exercises → BICEPS secondary (+1 on pull day)
        { 6,  "BICEPS" }, // Seated Cable Row
        { 7,  "BICEPS" }, // Lat Pulldown
        { 8,  "BICEPS" }, // Close Grip Lat Pulldown
        { 9,  "BICEPS" }, // Smith Machine Row

        // Chest compounds → TRICEPS secondary (+1 on push day)
        { 12, "TRICEPS" },   // Bench Press
        { 13, "TRICEPS" },   // Smith Machine Incline Bench Press
        { 14, "TRICEPS" },   // Dumbbell Bench Press
        { 15, "SHOULDERS" }, // Incline Bench Press — hits front delt

        // Leg compounds → GLUTES secondary (+1 on legs day)
        { 16, "GLUTES" }, // Romanian Deadlift
        { 17, "GLUTES" }, // Barbell Squat

        // Shoulder press → TRICEPS secondary (+1 on push day)
        { 21, "TRICEPS" }, // Overhead Press

        // New bodyweight compounds → relevant secondaries
        { 24, "BICEPS" },  // Pull Up — back compound, hits biceps (+1 on pull day)
        { 25, "TRICEPS" }, // Push Up — chest compound, hits triceps (+1 on push day)
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
        { 1,  20.0, 35.0, 50.0 }, // Cable Bicep Curl
        { 2,  20.0, 35.0, 50.0 }, // Cable Hammer Curl
        { 3,  15.0, 25.0, 40.0 }, // Bicep Curl (per dumbbell)

        // Triceps isolations
        { 4,  25.0, 45.0, 70.0 }, // Overhead Tricep Extension
        { 5,  30.0, 55.0, 85.0 }, // Tricep Pushdown

        // Back compounds
        { 6,  60.0, 110.0, 170.0 }, // Seated Cable Row
        { 7,  60.0, 110.0, 160.0 }, // Lat Pulldown
        { 8,  60.0, 110.0, 160.0 }, // Close Grip Lat Pulldown
        { 9,  65.0, 115.0, 175.0 }, // Smith Machine Row

        // Chest isolations (cables)
        { 10, 20.0, 35.0, 55.0 }, // Lower Cable Chest Fly
        { 11, 20.0, 35.0, 55.0 }, // Upper Cable Chest Fly

        // Chest compounds
        { 12, 65.0, 135.0, 205.0 }, // Bench Press
        { 13, 65.0, 115.0, 175.0 }, // Smith Machine Incline Bench Press
        { 14, 30.0,  55.0,  85.0 }, // Dumbbell Bench Press — per dumbbell
        { 15, 25.0,  50.0,  75.0 }, // Incline Bench Press — per dumbbell

        // Leg compounds
        { 16, 30.0,  55.0,  85.0 }, // Romanian Deadlift (per dumbbell)
        { 17, 65.0, 155.0, 245.0 }, // Barbell Squat (includes the 45-lb bar)

        // Leg isolations
        { 18, 50.0,  90.0, 140.0 }, // Leg Extension
        { 19, 40.0,  75.0, 120.0 }, // Leg Curl
        { 20, 45.0,  90.0, 145.0 }, // Seated Calf Raise

        // Shoulder compound + chest fly + rear delt isolation
        { 21, 25.0,  45.0,  70.0 }, // Overhead Press (per dumbbell)
        { 22, 15.0,  25.0,  40.0 }, // Dumbbell Chest Fly (per dumbbell)
        { 23, 10.0,  20.0,  30.0 }, // Rear Delt Fly (per dumbbell)

        // NOTE: Pull Up / Push Up / Sit Up / Russian Twist / Leg Lift are bodyweight,
        // and Stairmaster / Treadmill are time-based cardio — none get starting weight
        // rows. ExercisesDAO.getStartingWeight() returns 0.0 for missing entries,
        // which puts the workout screen in the bodyweight UI branch.
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
        { 1,  "Cable Machine",          1 }, // Cable Bicep Curl
        { 2,  "Cable Machine",          1 }, // Cable Hammer Curl
        { 3,  "Dumbbells",              0 }, // Bicep Curl
        { 3,  "Incline Bench",          0 },

        // Triceps isolations
        { 4,  "Cable Machine",          1 }, // Overhead Tricep Extension
        { 4,  "Rope Attachment",        1 },
        { 5,  "Cable Machine",          1 }, // Tricep Pushdown

        // Back compounds
        { 6,  "Cable Machine",          1 }, // Seated Cable Row
        { 7,  "Lat Pulldown Machine",   1 }, // Lat Pulldown
        { 8,  "Lat Pulldown Machine",   1 }, // Close Grip Lat Pulldown
        { 9,  "Smith Machine",          1 }, // Smith Machine Row

        // Chest isolations (cables)
        { 10, "Cable Machine",          1 }, // Lower Cable Chest Fly
        { 11, "Cable Machine",          1 }, // Upper Cable Chest Fly

        // Chest compounds
        { 12, "Barbell",                0 }, // Bench Press
        { 12, "Flat Bench",             0 },
        { 13, "Smith Machine",          1 }, // Smith Machine Incline Bench Press
        { 13, "Incline Bench",          0 },
        { 14, "Dumbbells",              0 }, // Dumbbell Bench Press
        { 14, "Flat Bench",             0 },
        { 15, "Dumbbells",              0 }, // Incline Bench Press
        { 15, "Incline Bench",          0 },

        // Leg compounds
        { 16, "Dumbbells",              0 }, // Romanian Deadlift
        { 17, "Barbell",                0 }, // Barbell Squat
        { 17, "Squat Rack",             0 },

        // Leg isolations
        { 18, "Leg Extension Machine",  1 }, // Leg Extension
        { 19, "Leg Curl Machine",       1 }, // Leg Curl
        { 20, "Calf Raise Machine",     1 }, // Seated Calf Raise

        // Shoulder compound + chest fly + rear delt
        { 21, "Dumbbells",              0 }, // Overhead Press
        { 22, "Dumbbells",              0 }, // Dumbbell Chest Fly
        { 22, "Flat Bench",             0 },
        { 23, "Dumbbells",              0 }, // Rear Delt Fly

        // Bodyweight movements
        { 24, "Pull-up Bar",            3 }, // Pull Up
        { 25, "Floor Mat",              3 }, // Push Up
        { 26, "Floor Mat",              3 }, // Sit Up
        { 27, "Floor Mat",              3 }, // Russian Twist
        { 28, "Floor Mat",              3 }, // Leg Lift

        // Cardio machines
        { 29, "Stair Climber",          2 }, // Stairmaster
        { 30, "Treadmill",              2 }, // Treadmill
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

            // one-time renames: patch any stale names that survived INSERT OR IGNORE
            // from earlier seed runs. Safe to run every launch — no-op if already correct.
            conn.createStatement().execute(
                "UPDATE exercises SET name = 'Smith Machine Row' " +
                "WHERE exerciseId = 9 AND name = 'Barbell Row'"
            );

            // one-time equipment fix: Overhead Tricep Extension uses a cable + rope,
            // not a dumbbell. Delete the wrong row and let the equipment guard
            // re-seed it correctly on first launch after this change.
            conn.createStatement().execute(
                "DELETE FROM equipment WHERE exerciseId = 4 AND name = 'Dumbbell'"
            );
            // if no cable row exists yet for exercise 4, insert both correct rows now
            java.sql.ResultSet ex4Rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM equipment WHERE exerciseId = 4 AND name = 'Cable Machine'"
            );
            boolean ex4AlreadyFixed = ex4Rs.next() && ex4Rs.getInt(1) > 0;
            ex4Rs.close();
            if (!ex4AlreadyFixed) {
                conn.createStatement().execute(
                    "INSERT INTO equipment (exerciseId, name, EquipmentType, isCustom) VALUES (4, 'Cable Machine', 1, 0)"
                );
                conn.createStatement().execute(
                    "INSERT INTO equipment (exerciseId, name, EquipmentType, isCustom) VALUES (4, 'Rope Attachment', 1, 0)"
                );
            }

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

            System.out.println("DatabaseSeeder: " + EXERCISES.length + " exercises seeded successfully.");

        } catch (Exception e) {
            System.out.println("DatabaseSeeder error: " + e.getMessage());
        }
    }
}
