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

    // id for this workout plan
    private String planId;

    // if for which user this plan belongs too
    private String userId;

    // name of the workout plan which is hopefully user friendly
    // ex "Summer Shred Program" and "12-Wek Muscle Building"
    private String planName;

    // the primary goal that this plan is designed for
    // this can help determine exercise selection, sets, reps, and res time
    // Examples should be like : "muscle building" or "strength" or "weight loss" etc
    // note: that this should the GoalType enum values
    private String goal;

    // how many weeks this program lasts
    // typical ranges: 4-16 weeks
    // example: 12 weeks = 3 month program
    private int durationWeeks;

    // which week the user is currently on
    // used for progressive overload
    // example week 3 of 12 means the user is 25% through the program
    // the progressive overload would be like : w1 = 50lbs, w2 = 55, w3 = 60
    private int currentWeek;

    // when this plan started
    // it is used to calculate rest periods and show progress over time
    // example: started 03/31/2026
    private LocalDate startDate;

    // is the plan currently active
    // a rule of our program is that the user can only have 1 active plan at a time
    /*
    * some of the scenarios would be if user creates/starts plan A -> isActive = true
    * if the user completes plan A -> isActive is false
    * */
    private boolean isActive;


    // this hashmap will make a day to given workout
    // for example: Monday -> push, Tuesday -> pull, etc...
    // this is a plan for each week that repeats weekly
    // same exercises each week, but weights increase
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

    public String getPlanId (){return planId;}
    public void setPlanId (String planId){this.planId = planId;}

    public String getUserId (){return userId;}
    public void setUserId (String userId){this.userId = userId;}

    public String getPlanName (){return planName;}
    public void setPlanName (String planName){this.planName = planName;}

    public String getGoal (){return goal;}
    public void setGoal (String goal){this.goal = goal;}

    public int getDurationWeeks() {return durationWeeks;}
    public void setDurationWeeks(int weeks) {this.durationWeeks = weeks;}

    public int getCurrentWeek() {return currentWeek;}
    public void setCurrentWeek(int week) {this.currentWeek = week;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate date) {this.startDate = date;}

    public boolean isActive() {return isActive;}
    public void setActive(boolean active) {this.isActive = active;}

    public Map<DayOfWeek, DayWorkoutPlan> getWeeklyTemplate() {return weeklyPlan;}
    public void setWeeklyTemplate(Map<DayOfWeek, DayWorkoutPlan> plan) {this.weeklyPlan = plan;}

    @Override
    public String toString() {
        return planName + " (Week " + currentWeek + "/" + durationWeeks + ")";
    }
}

