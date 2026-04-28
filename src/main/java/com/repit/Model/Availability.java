package com.repit.Model;

import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Availability
 * Represents the days and minutes per day a user is free to train each week.
 *
 * This is different from FitnessProfile.daysPerWeek, which is just a count.
 * Availability stores the actual specific days and how long the user has on each one.
 * Example: Monday → 60 min, Wednesday → 45 min, Friday → 30 min.
 *
 * Why per-day minutes matter:
 * A user might have a full hour on Monday but only 30 minutes on Friday.
 * PlannerService uses these per-day values when calling ExercisePriorityQueue
 * so the exercise count adjusts to the time available on that specific day
 * rather than using the same count every session.
 *
 * Relationship to other models:
 * - FitnessProfile: stores the general preference (daysPerWeek, minsAvailablePerWorkout)
 * - Availability:   stores the specific schedule (which days, how long each day)
 */
public class Availability {

    // which user this availability record belongs to
    private int userId;

    /*
     * Maps each training day to how many minutes the user has available on that day.
     * Days not present in this map are rest days — no entry needed for them.
     * Uses LinkedHashMap to preserve Monday-first calendar order.
     *
     * Example:
     *   MONDAY    → 60
     *   WEDNESDAY → 45
     *   FRIDAY    → 30
     */
    private Map<DayOfWeek, Integer> minutesPerDay;

    // ─────────────────────────────────────────────────────────────────────────
    // Constructors
    // ─────────────────────────────────────────────────────────────────────────

    public Availability() {
        this.minutesPerDay = new LinkedHashMap<>();
    }

    public Availability(int userId) {
        this.userId = userId;
        this.minutesPerDay = new LinkedHashMap<>();
    }

    public Availability(int userId, Map<DayOfWeek, Integer> minutesPerDay) {
        this.userId = userId;
        // copy into a LinkedHashMap so calendar order is always preserved
        this.minutesPerDay = new LinkedHashMap<>(minutesPerDay);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Core getters and setters
    // ─────────────────────────────────────────────────────────────────────────

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Map<DayOfWeek, Integer> getMinutesPerDay() { return minutesPerDay; }
    public void setMinutesPerDay(Map<DayOfWeek, Integer> minutesPerDay) {
        this.minutesPerDay = new LinkedHashMap<>(minutesPerDay);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Convenience methods
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Adds or updates a single training day with its available minutes.
     * Calling this with a day already in the map replaces the old value.
     *
     * @param day     the day to mark as a training day
     * @param minutes how many minutes are available on that day (must be > 0)
     */
    public void setMinutesForDay(DayOfWeek day, int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException(
                    "minutes must be positive, got: " + minutes + " for " + day);
        }
        minutesPerDay.put(day, minutes);
    }

    /**
     * Returns how many minutes the user has available on a given day.
     * Returns 0 if that day is not a training day (i.e., it's a rest day).
     *
     * @param day the day to look up
     * @return available minutes, or 0 if it's a rest day
     */
    public int getMinutesForDay(DayOfWeek day) {
        return minutesPerDay.getOrDefault(day, 0);
    }

    /**
     * Removes a day from the training schedule, effectively making it a rest day.
     *
     * @param day the day to remove
     */
    public void removeDay(DayOfWeek day) {
        minutesPerDay.remove(day);
    }

    /**
     * Returns true if the user has this day marked as a training day.
     * A day is a training day if it exists in the map and has minutes > 0.
     *
     * @param day the day to check
     * @return true if this is a training day, false if it is a rest day
     */
    public boolean isTrainingDay(DayOfWeek day) {
        return minutesPerDay.containsKey(day) && minutesPerDay.get(day) > 0;
    }

    /**
     * Returns the set of days the user has marked as training days.
     * The set is in calendar order (Monday through Sunday) because we use LinkedHashMap.
     * PlannerService passes this directly to SplitSelector.selectSplit().
     *
     * @return set of training days, empty if the user has not set up their schedule yet
     */
    public Set<DayOfWeek> getAvailableDays() {
        return minutesPerDay.keySet();
    }

    /**
     * Returns how many days per week the user trains.
     * Equivalent to getAvailableDays().size().
     * Useful for quick checks without iterating the full map.
     *
     * @return number of training days
     */
    public int getDaysPerWeek() {
        return minutesPerDay.size();
    }

    /**
     * Returns the total minutes across all training days this week.
     * Useful for displaying the user's full weekly time commitment.
     * Example: Mon(60) + Wed(45) + Fri(30) → 135 total minutes.
     *
     * @return sum of all per-day minutes
     */
    public int getTotalWeeklyMinutes() {
        int total = 0;
        for (int minutes : minutesPerDay.values()) {
            total += minutes;
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Availability{userId=" + userId + ", schedule=[");
        minutesPerDay.forEach((day, mins) ->
                sb.append(day.name(), 0, 3).append(":").append(mins).append("min "));
        sb.append("]}");
        return sb.toString();
    }
}
