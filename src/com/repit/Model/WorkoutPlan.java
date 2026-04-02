package com.repit.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the complete workout plan for the week
 * Contains workouts for each day of the week
 */
public class WorkoutPlan {

    private String planId;
    private String userId;
    private String planName;
    private String goal;            // losing muscle, strength, loosing weight, etc...
    private int durationWeeks;
    private int currentWeek;
    private LocalDate startDate;
    private boolean isActive;


    // this hashmap will make a day to given workout
    // for example: Monday -> push, Tuesday -> pull, etc...

    private Map <DayOfWeek,DayWorkoutPlan> weeklyPlan;

    // constructor

    public WorkoutPlan () {
        this.weeklyPlan = new HashMap<>();
        this.currentWeek = 1;
        this.isActive = true;
    }

    // Convenience methods
    public void setDayWorkout (DayOfWeek day, DayWorkoutPlan workout){
        workout.setDayOfWeek(day);
        weeklyPlan.put(day,workout);
    }

    public DayWorkoutPlan getDayWorkout (DayOfWeek day){
        return weeklyPlan.get(day);
    }

    // Getters and setters

    public String getPlanId (){
        return planId;
    }
    public void setPlanId (String planId){
        this.planId = planId;
    }

    public String getUserId (){
        return userId;
    }
    public void setUserId (String userId){
        this.userId = userId;
    }

    public String getPlanName (){
        return planName;
    }
    public void setPlanName (String planName){
        this.planName = planName;
    }

    public String getGoal (){
        return goal;
    }
    public void setGoal (String goal){
        this.goal = goal;
    }
    public int getDurationWeeks() {
        return durationWeeks;
    }
    public void setDurationWeeks(int weeks) {
        this.durationWeeks = weeks;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }
    public void setCurrentWeek(int week) {
        this.currentWeek = week;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate date) {
        this.startDate = date;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        this.isActive = active;
    }

    public Map<DayOfWeek, DayWorkoutPlan> getWeeklyTemplate() {
        return weeklyPlan;
    }
    public void setWeeklyTemplate(Map<DayOfWeek, DayWorkoutPlan> plan) {
        this.weeklyPlan = plan;
    }

    @Override
    public String toString() {
        return planName + " (Week " + currentWeek + "/" + durationWeeks + ")";
    }
}

