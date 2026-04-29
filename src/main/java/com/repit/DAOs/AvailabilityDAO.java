package com.repit.DAOs;

import com.repit.Model.Availability;
import com.repit.Model.Equipment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

/**
 * AvailabilityDAO
 * Stores and retrieves the user's available training days and minutes per day.
 *
 * Right now, PlannerService uses a default day pattern based on daysPerWeek
 * (e.g., 3 days → always Monday, Wednesday, Friday). This DAO will replace
 * that default by letting the user pick their own specific days and times.
 *
 * What this DAO needs to do:
 *
 *  1. saveAvailability(Availability availability)
 *     - Persist the full Availability object for a user.
 *     - Iterates availability.getMinutesPerDay() and writes one row per training day.
 *     - Replaces any existing rows for that userId (delete-then-insert pattern).
 *     - Days not in the map are implicitly rest days — no row needed for them.
 *
 *  2. getAvailability(int userId) -> Availability
 *     - Reads all rows for that userId and reconstructs the Availability object.
 *     - Returns an empty Availability (no training days) if the user has not set one up yet.
 *     - PlannerService calls availability.getAvailableDays() to pass into SplitSelector,
 *       and availability.getMinutesForDay(day) for the per-day time budget.
 *
 *  3. updateMinutesForDay(int userId, DayOfWeek day, int minutes)
 *     - Updates a single day's available minutes without touching the rest of the schedule.
 *     - Uses an INSERT OR REPLACE (upsert) so it works whether the row exists or not.
 *     - Useful for the planner screen when the user only wants to shorten one day.
 *
 *  4. removeDay(int userId, DayOfWeek day)
 *     - Deletes the row for that day, making it a rest day.
 *     - Called when the user unchecks a day on the availability picker screen.
 *
 *  Database table (availability):
 *    CREATE TABLE IF NOT EXISTS availability (
 *      userId   INTEGER NOT NULL,
 *      dayOfWeek INTEGER NOT NULL,   -- DayOfWeek.getValue(): MONDAY=1 ... SUNDAY=7
 *      minutes  INTEGER NOT NULL,
 *      PRIMARY KEY (userId, dayOfWeek)
 *    )
 *
 * Wiring into PlannerService (once this DAO is implemented):
 *   In PlannerService.getWeeklyPlan(), replace:
 *     Set<DayOfWeek> trainingDays = getDefaultTrainingDays(profile.getDaysPerWeek());
 *   with:
 *     Availability availability = availabilityDAO.getAvailability(userId);
 *     Set<DayOfWeek> trainingDays = availability.getAvailableDays();
 *
 *   And inside the day loop, replace the fixed availableMinutes with:
 *     int availableMinutes = snapToTenMinBlock(availability.getMinutesForDay(day));
 */
public class AvailabilityDAO extends BaseDAO {

    private static final String TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS availabilities (" +
                    "userId INTEGER NOT NULL," +
                    "dayOfWeek INTEGER NOT NULL," +
                    "minutes INTEGER NOT NULL," +
                    "PRIMARY KEY (userId, dayOfWeek)" +
                    ")";

    private static final String INSERT_SQL =
            "INSERT INTO availabilities (userId, dayOfWeek, minutes) "+
                    "VALUES (?,?,?)";
    private static final String SELECT_DAYS_SQL =
            "SELECT * FROM availabilities WHERE userId = ?";

    private static final String UPDATE_SQL =
            "UPDATE availabilities SET dayOfWeek=?, minutes=? WHERE userId=?";
    private static final String DELETE_SQL =
            "DELETE FROM availabilities WHERE userId = ? AND dayOfWeek = ?";


    public AvailabilityDAO() {
        super();
    }

    public boolean saveAvailability(Availability availability){
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            Map<DayOfWeek, Integer> minutesPerDay = availability.getMinutesPerDay();
            for (Map.Entry<DayOfWeek, Integer> entry : minutesPerDay.entrySet()) {
                DayOfWeek day = entry.getKey();
                Integer minutes = entry.getValue();

                PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
                pstmt.setInt(1, availability.getUserId());
                pstmt.setInt(2, day.ordinal());
                pstmt.setInt(3, minutes);
                pstmt.executeUpdate();
            }

            return true;
        } catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Availability getAvailability(int userId){
        Map<DayOfWeek, Integer> minutesPerDay = new HashMap<>();
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);


            PreparedStatement pstmt = connection.prepareStatement(SELECT_DAYS_SQL);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                DayOfWeek day = DayOfWeek.values()[rs.getInt("dayOfWeek")];
                int minutes = rs.getInt("minutes");
                minutesPerDay.put(day, minutes);
            }
            return new Availability(userId, minutesPerDay);

        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean updateAvailability(Availability availability){
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            int userId = availability.getUserId();
            availability.getMinutesPerDay();
            Map<DayOfWeek, Integer> minutesPerDay = availability.getMinutesPerDay();
            for (Map.Entry<DayOfWeek, Integer> entry : minutesPerDay.entrySet()) {
                DayOfWeek day = entry.getKey();
                Integer minutes = entry.getValue();

                PreparedStatement pstmt = connection.prepareStatement(UPDATE_SQL);
                pstmt.setInt(3, userId);
                pstmt.setInt(1, day.ordinal());
                pstmt.setInt(2, minutes);
                pstmt.executeUpdate();
            }

            return true;
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean removeAvailability(int userId, DayOfWeek day){
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(TABLE_SQL);

            PreparedStatement pstmt = connection.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, day.ordinal());
            pstmt.executeUpdate();
            return true;
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return  false;
    }

    // TODO: implement saveAvailability(Availability availability)
    //       delete all existing rows for userId, then insert one row per training day

    // TODO: implement getAvailability(int userId) -> Availability
    //       query all rows where userId = ?, build and return Availability object

    // TODO: implement updateMinutesForDay(int userId, DayOfWeek day, int minutes)
    //       INSERT OR REPLACE INTO availability (userId, dayOfWeek, minutes) VALUES (?, ?, ?)

    // TODO: implement removeDay(int userId, DayOfWeek day)
    //       DELETE FROM availability WHERE userId = ? AND dayOfWeek = ?`
}
