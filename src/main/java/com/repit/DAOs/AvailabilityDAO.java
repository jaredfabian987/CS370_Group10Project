package com.repit.DAOs;

import com.repit.Model.Availability;

import java.time.DayOfWeek;

/**
 * AvailabilityDAO
 * Stores and retrieves the user's available training days and minutes per day.
 *
 * Right now, PlannerService uses a default day pattern based on daysPerWeek
 * (e.g., 3 days → always Monday, Wednesday, Friday). This DAO will replace
 * that default by letting the user pick their own specific days and times.
 *
 * Rest day rule:
 * Rest days are NOT stored in this table. They are derived automatically —
 * any day of the week that does NOT appear as a row for this user is a rest day.
 * For users with 6 or 7 training days there are no rest days from availability;
 * the split simply continues every day of the week with no gaps.
 *
 * What this DAO needs to do:
 *
 *  1. saveAvailability(Availability availability)
 *     - Persist the full Availability object for a user.
 *     - Iterates availability.getMinutesPerDay() and writes one row per training day.
 *     - Replaces any existing rows for that userId (delete-then-insert pattern).
 *     - Days not in the map have no row — PlannerService treats them as rest days.
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
            "CREATE TABLE IF NOT EXISTS availability (" +
                    "userId INTEGER NOT NULL," +
                    "dayOfWeek INTEGER NOT NULL," +
                    "minutes INTEGER NOT NULL," +
                    "PRIMARY KEY (userId, dayOfWeek)" +
                    ")";

    public AvailabilityDAO() {
        super();
    }

    // TODO: implement saveAvailability(Availability availability)
    //       delete all existing rows for userId, then insert one row per training day

    // TODO: implement getAvailability(int userId) -> Availability
    //       query all rows where userId = ?, build and return Availability object

    // TODO: implement updateMinutesForDay(int userId, DayOfWeek day, int minutes)
    //       INSERT OR REPLACE INTO availability (userId, dayOfWeek, minutes) VALUES (?, ?, ?)

    // TODO: implement removeDay(int userId, DayOfWeek day)
    //       DELETE FROM availability WHERE userId = ? AND dayOfWeek = ?
}
