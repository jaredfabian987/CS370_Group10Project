package com.repit.model;


import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Represent the workout for one day
 * contains a list of exercises for that dayh
 */

public class DayWorkoutPlan {

    private String dayPlanId;
    private String planId;              // which workout the given exercise belongs to
    private DayOfWeek dayOfWeek;        // Monday, Tuesday ,....
    private String workoutName;         // push, pull, upper, etc...
    private boolean isRestDay;          // is it a rest day?
    private List<PlannedExercise> exercises;
    private int estimatedDurationMinutes;

    // constructor
    public DayWorkoutPlan() {
        this.exercises = new ArrayList<>();
        this.isRestDay = false;
    }

    public DayWorkoutPlan (DayOfWeek day){
        this();
        this.dayOfWeek = day;
    }

    // method to add an exercise
    public void addExercise (PlannedExercise exercise){
        exercise.setExerciseOrder(exercises.size() + 1);
        exercises.add(exercise);
    }

    // getters and setters
    public String getDayPlanId() {
        return dayPlanId;
    }
    public void setDayPLanId(String id){
        this.dayPlanId = id;
    }

    public String getPlanId() {
        return planId;
    }
    public void setPlanId(String id){
        this.planId = id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(DayOfWeek dayOfWeek){
        this.dayOfWeek = dayOfWeek;
    }

    public String getWorkoutName() {
        return workoutName;
    }
    public void setWorkoutName(String name){
        this.workoutName = name;
    }

    public boolean isRestDay() {
        return isRestDay;
    }
    public void setRestDay(boolean restDay) {
        isRestDay = restDay;
    }

    public List<PlannedExercise> getExercises(){
        return exercises;
    }

    public void setExercises(List<PlannedExercise> exercises) {
        this.exercises = exercises;
    }

    public int getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }
    /*
    @Override
    public String toString(){
        if (isRestDay){
            return dayOfWeek + ": REST DAY";
        }
        return dayOfWeek + ": " + workoutName + " (" exercises.size() + " exercises";
    }
     */
}
