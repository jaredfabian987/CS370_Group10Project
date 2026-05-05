package com.repit.DAOs;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * DemoDataSeeder
 * Creates a demo user with one full week of completed workouts already in
 * the books. Used to verify progressive-overload behavior: PlannerService
 * pulls last-week's reps + weight through ProgressService.suggestProgression()
 * and bumps the working weight when reps >= 10.
 *
 * Demo user: ADV (proficient lifter), 6-day MUSCLE_BUILDING split.
 * Last week's logs are dated to LAST week's Mon-Sat so the dashboard shows
 * a fresh "0 / 6 workouts" for the current week — but progression triggers
 * the moment the user starts a workout because the most recent log per
 * exercise is from last week with reps = 10 (the threshold).
 *
 * Login: demo / demo123
 *
 * Idempotent: skips if the demo user already exists.
 *
 * Wiring (one line in Main.start() right after DatabaseSeeder.seed()):
 *     DemoDataSeeder.seedDemoUser();
 *
 * EXPECTED PROGRESSION ON DAY 1 OF THIS WEEK (ADV starting weights):
 *   Bench Press                205 → 220 lbs
 *   Smith Machine Incline      175 → 190 lbs
 *   Overhead Press              70 →  75 lbs
 *   Tricep Pushdown             85 →  90 lbs
 *   Lateral Raise (chest fly)   40 →  40 lbs (rounding flattens it)
 *   Smith Machine Row          175 → 190 lbs
 *   Lat Pulldown               160 → 175 lbs
 *   Cable Row                  170 → 185 lbs
 *   Cable Bicep Curl            50 →  55 lbs
 *   Cable Hammer Curl           50 →  55 lbs
 *   Barbell Squat              245 → 265 lbs
 *   Romanian Deadlift           85 →  90 lbs
 *   Leg Extension              140 → 150 lbs
 *   Leg Curl                   120 → 130 lbs
 *   Seated Calf Raise          145 → 155 lbs
 */
public class DemoDataSeeder {

    private static final String DB_URL = "jdbc:sqlite:repit.db";

    private static final String DEMO_USERNAME    = "demo";
    private static final String DEMO_PASSWORD    = "demo123";
    private static final String DEMO_FIRST_NAME  = "Demo";
    private static final String DEMO_LAST_NAME   = "User";
    private static final String DEMO_DOB         = "2000-01-01";

    /*
     * Workout templates per day type — ADV starting weights × 10 reps.
     * 10 reps is the progression threshold; ProgressService will compute a
     * heavier suggested weight via the Epley formula targeting 7 reps.
     * Format: { exerciseId, weight, reps }
     */
    private static final Object[][] PUSH_DAY = {
        { 12, 205.0, 10 }, // Bench Press
        { 13, 175.0, 10 }, // Smith Machine Incline Bench Press
        { 21,  70.0, 10 }, // Overhead Press
        {  5,  85.0, 10 }, // Tricep Pushdown
        { 22,  40.0, 10 }, // Dumbbell Chest Fly
    };

    private static final Object[][] PULL_DAY = {
        {  9, 175.0, 10 }, // Smith Machine Row
        {  7, 160.0, 10 }, // Lat Pulldown
        {  6, 170.0, 10 }, // Seated Cable Row
        {  1,  50.0, 10 }, // Cable Bicep Curl
        {  2,  50.0, 10 }, // Cable Hammer Curl
    };

    private static final Object[][] LEGS_DAY = {
        { 17, 245.0, 10 }, // Barbell Squat
        { 16,  85.0, 10 }, // Romanian Deadlift
        { 18, 140.0, 10 }, // Leg Extension
        { 19, 120.0, 10 }, // Leg Curl
        { 20, 145.0, 10 }, // Seated Calf Raise
    };

    public static void seedDemoUser() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (findExistingDemoUserId(conn) != -1) {
                System.out.println("DemoDataSeeder: demo user already exists (skipping).");
                return;
            }

            int userId = createDemoUser(conn);
            if (userId == -1) {
                System.out.println("DemoDataSeeder: failed to create demo user.");
                return;
            }

            createDemoProfile(conn, userId);
            createDemoAvailability(conn, userId);

            // Logs are dated to LAST week's Mon–Sat. The dashboard reads the
            // current Mon–Sun for "this week's progress" so it'll show 0 / 6,
            // but ProgressService walks ALL history → finds last week's logs
            // (10 reps each) → triggers progression on Day 1 of this week.
            LocalDate lastMonday = LocalDate.now()
                    .with(DayOfWeek.MONDAY)
                    .minusWeeks(1);

            seedDayLogs(conn, userId, lastMonday,             PUSH_DAY); // last Mon
            seedDayLogs(conn, userId, lastMonday.plusDays(1), PULL_DAY); // last Tue
            seedDayLogs(conn, userId, lastMonday.plusDays(2), LEGS_DAY); // last Wed
            seedDayLogs(conn, userId, lastMonday.plusDays(3), PUSH_DAY); // last Thu
            seedDayLogs(conn, userId, lastMonday.plusDays(4), PULL_DAY); // last Fri
            seedDayLogs(conn, userId, lastMonday.plusDays(5), LEGS_DAY); // last Sat

            System.out.println("DemoDataSeeder: demo user seeded with last week's logs (login: demo / demo123).");

        } catch (Exception e) {
            System.out.println("DemoDataSeeder error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int findExistingDemoUserId(Connection conn) throws Exception {
        conn.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS users (" +
            "userId INTEGER PRIMARY KEY," +
            "username INTEGER UNIQUE NOT NULL," +
            "password TEXT NOT NULL," +
            "firstName TEXT NOT NULL," +
            "lastName TEXT NOT NULL," +
            "date_of_birth TEXT NOT NULL)"
        );
        PreparedStatement pstmt = conn.prepareStatement(
            "SELECT userId FROM users WHERE username = ?");
        pstmt.setString(1, DEMO_USERNAME);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) return rs.getInt("userId");
        return -1;
    }

    private static int createDemoUser(Connection conn) throws Exception {
        String hashedPassword = BCrypt.hashpw(DEMO_PASSWORD, BCrypt.gensalt());
        PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO users (username, password, firstName, lastName, date_of_birth) " +
            "VALUES (?, ?, ?, ?, ?)",
            java.sql.Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, DEMO_USERNAME);
        pstmt.setString(2, hashedPassword);
        pstmt.setString(3, DEMO_FIRST_NAME);
        pstmt.setString(4, DEMO_LAST_NAME);
        pstmt.setString(5, DEMO_DOB);
        pstmt.executeUpdate();
        ResultSet keys = pstmt.getGeneratedKeys();
        if (keys.next()) return keys.getInt(1);
        return -1;
    }

    private static void createDemoProfile(Connection conn, int userId) throws Exception {
        conn.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS profiles (" +
            "profileId INTEGER PRIMARY KEY," +
            "userId INTEGER NOT NULL UNIQUE," +
            "weight REAL NOT NULL," +
            "height REAL NOT NULL," +
            "daysPerWeek INTEGER NOT NULL," +
            "minsAvailablePerWorkout INTEGER NOT NULL," +
            "level INTEGER NOT NULL," +
            "goal INTEGER NOT NULL)"
        );
        PreparedStatement pstmt = conn.prepareStatement(
            "INSERT OR REPLACE INTO profiles " +
            "(userId, weight, height, daysPerWeek, minsAvailablePerWorkout, level, goal) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)");
        pstmt.setInt(1,    userId);
        pstmt.setDouble(2, 175.0); // weight
        pstmt.setDouble(3, 70.0);  // height
        pstmt.setInt(4,    6);     // daysPerWeek
        pstmt.setInt(5,    50);    // minsAvailablePerWorkout
        pstmt.setInt(6,    2);     // level: ADV
        pstmt.setInt(7,    2);     // goal:  MUSCLE
        pstmt.executeUpdate();
    }

    private static void createDemoAvailability(Connection conn, int userId) throws Exception {
        conn.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS availabilities (" +
            "userId INTEGER NOT NULL," +
            "dayOfWeek INTEGER NOT NULL," +
            "minutes INTEGER NOT NULL," +
            "PRIMARY KEY (userId, dayOfWeek))"
        );
        PreparedStatement clear = conn.prepareStatement(
            "DELETE FROM availabilities WHERE userId = ?");
        clear.setInt(1, userId);
        clear.executeUpdate();

        PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO availabilities (userId, dayOfWeek, minutes) VALUES (?, ?, ?)");
        DayOfWeek[] trainingDays = {
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        };
        for (DayOfWeek day : trainingDays) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, day.ordinal());
            pstmt.setInt(3, 50);
            pstmt.executeUpdate();
        }
    }

    private static void seedDayLogs(Connection conn, int userId, LocalDate date,
                                     Object[][] dayTemplate) throws Exception {
        conn.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS workout_logs (" +
            "logId INTEGER PRIMARY KEY," +
            "userId INTEGER NOT NULL," +
            "exerciseId INTEGER NOT NULL," +
            "isCompleted INTEGER NOT NULL," +
            "date TEXT NOT NULL," +
            "reps INTEGER NOT NULL DEFAULT 0," +
            "weight REAL NOT NULL DEFAULT 0)"
        );
        String dateStr = date.toString();
        PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO workout_logs " +
            "(userId, exerciseId, isCompleted, date, reps, weight) " +
            "VALUES (?, ?, 1, ?, ?, ?)");

        for (Object[] row : dayTemplate) {
            int exerciseId = (int) row[0];
            double weight  = (double) row[1];
            int reps       = (int) row[2];
            // 2 working sets per exercise (matches PlannedExercise default workingSets=2)
            for (int setNum = 0; setNum < 2; setNum++) {
                pstmt.setInt(1,    userId);
                pstmt.setInt(2,    exerciseId);
                pstmt.setString(3, dateStr);
                pstmt.setInt(4,    reps);
                pstmt.setDouble(5, weight);
                pstmt.executeUpdate();
            }
        }
    }
}
