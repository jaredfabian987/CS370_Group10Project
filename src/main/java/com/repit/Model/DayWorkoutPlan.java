package com.repit.Model;


import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Represent the workout for one day
 * contains a list of exercises for that day
 */


// represents the workout for one specific day, contains a list of the
    // exercise to perform on that day
public class DayWorkoutPlan {

    // unique id for this day's workout plan
    private String dayPlanId;

    // unique id for which workout plan this day belongs to
    // relationship to parent plan
    private String planId;

    //which day of the week this workout is for
    // use java util
    private DayOfWeek dayOfWeek;

    // nae fo the workout for this day
    // user friendly label that describes the focus
    // for example like: push day , pull day, legs
    private String workoutName;

    // is the current day a rest day meaning no workout
    // rest days are intentional and are recommended but are also
    // catered to user availability
    private boolean isRestDay;

    /*
     * Tracks whether the user has finished every exercise on this day's plan.
     * PlannerService sets this to true when the user's logged dates this week
     * include this day's date. Used by the dashboard to mark day cards as done
     * and to drive the "n out of n workouts completed" progress banner.
     */
    private boolean isCompleted;

    // order list of exercises for this day
    // each planned exercise contains: exercise, sets, reps, weight, rest time
    private List<PlannedExercise> exercises;

    // estimated total during of this workout in minutes
    // calculated from exercises, sets, rest time

    /**
     * come back to this to create a formulate to calculate this
     */
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
        //exercise.setExerciseOrder(exercises.size() + 1);
        exercises.add(exercise);
    }

    // getters and setters
    public String getDayPlanId() {return dayPlanId;}
    public void setDayPLanId(String id){this.dayPlanId = id;}

    public String getPlanId() {return planId;}
    public void setPlanId(String id){this.planId = id;}

    public DayOfWeek getDayOfWeek() {return dayOfWeek;}
    public void setDayOfWeek(DayOfWeek dayOfWeek){this.dayOfWeek = dayOfWeek;}

    public String getWorkoutName() {return workoutName;}
    public void setWorkoutName(String name){this.workoutName = name;}

    public boolean isRestDay() {return isRestDay;}
    public void setRestDay(boolean restDay) {isRestDay = restDay;}

    public boolean isCompleted() {return isCompleted;}
    public void setCompleted(boolean completed) {this.isCompleted = completed;}

    public List<PlannedExercise> getExercises(){return exercises;}

    public void setExercises(List<PlannedExercise> exercises) {this.exercises = exercises;}

    public int getEstimatedDurationMinutes() {return estimatedDurationMinutes;}
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) {this.estimatedDurationMinutes = estimatedDurationMinutes;}
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
