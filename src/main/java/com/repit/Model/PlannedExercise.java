package com.repit.Model;


/**
 * Model layer
 *
 *
 * Represent a planned exercise in a workout
 *
 * Example:
 * - Exercise: Bench Press
 * - Sets: 3 (2 Warmup + 2- working)
 * - Reps: 10
 * - Weight: 60lbs
 *
 * What is a PLannedExercise
 * - It is Not the same as Exercise.java which is just a exercise definition
 *  for example something like bench press etc...
 *  In this class it is more so like: DSo bench press for 60lbs for 3 sets of 10 reps on monday
 *  Planned Exercise is an exercise with context
 *
 */
public class PlannedExercise {

    // will store the ID of the given exercise to be referenced in the database
    private String plannedExerciseId;

    // exercise object will contain: name, category, difficulty, instructions, etc..
    // Note tha the relationship is: "PlannedExercise Has-A Exercise" and is not using inheritence
    // this is because planned exercise is not a type of exercise, it's more so a usage of an exerciseds
    // but with a specific context within the workout
    private Exercise exercise;



    /*
     * This will help us track the order of the exercises within the workout because
     * exercies in a workout have a specific sequence, for example, on a push day:
     * 1) Bench Press (compound movements should be done when your most fresh)
     * 2) Shoulder Press
     * 3) Tricep Extensions (isolation movement which you usually do last)
     * This will sequentially order exercies in 1, 2,3,4, etc..
     * */
    private int exerciseOrder;

    /*
     * Planned sets is the total amount of sets for a given movement which is the sum
     * warmup sets + working sets
     * Example: 2 warmup + 3 working = 5 total
     * */
    private int plannedSets;

    /*
     * Target reps for a working set,
     * Example is: "2 sets of 8 reps" for all sets the target reps is 8
     * Warmup sets have different rep quantities and are handled seperately
     */

    private int targetReps;

    /*
     * The suggested weight for a given set of an exercise is a RECOMMENDATION and is not absolute
     * This is because the user might need to adjust based on: how they feel that day, available equipment
     * or because of the quality of their form
     */
    private double suggestWeight;

    /*
     * This is prescribed rest not actual rest that is taken, the app can time the rest and notify the user
     * This value can vary based on the avalible time that the user alloted for a given workout
     * */
    private int restMinutes;

    // Will allow the user to include notes or coaching cues like: "focus on form" or
    // " pause at the bottom for 2 seconds" or "if you get 12 reps, increase the weight next week"
    private String notes;

    // these have a different purpose than working sets because for example
    // the user different weights like 50%, 75% of their normal working weight
    // or they also might do 10 or 12 reps of a lighter weight to prepare the body but not cause fatigue
    /*
     *  example would be: if I can bench 200 lbs for 8-10 (to failure)
     * My first warm up set I might do 100lbs for like 12 reps which is half my load
     * My second warm up set I might do 150lbs for 6 reps
     * */
    private int warmupSets;

    // working sets are the ones logged for calculating a users progress
    // these are sets completed with your working weight and are taken to musclar failure
    private int workingSets;

    /*
     * Tracks whether the user has finished this exercise during today's session.
     * Set to true when the last working set is logged for this exercise.
     * Used by PlannerService and the dashboard progress bar (n out of n completed).
     * Defaults to false — every exercise starts incomplete at the beginning of a workout.
     */
    private boolean isCompleted;

    // Constructors

    // default
    public PlannedExercise() {
        this.warmupSets = 2;        // default: 2 warmup sets
        this.workingSets = 2;       // default: 2 working set to failure
    }

    public PlannedExercise (Exercise exercise) {
        this();
        this.exercise = exercise;
    }

    // Getters and Setters
    public String plannedExerciseId() {return plannedExerciseId;}
    public void setPlannedExerciseId(String plannedExerciseId) {this.plannedExerciseId = plannedExerciseId;}

    public Exercise getExercise() {return exercise;}
    public void setExercise (Exercise exercise) {this.exercise = exercise;}

    public int getExerciseOrder() {return exerciseOrder;}
    public void setExerciseOrder(int exerciseOrder) {this.exerciseOrder = exerciseOrder;}

    public int getPlannedSets() {return plannedSets;}
    public void setPlannedSets(int plannedSets) {this.plannedSets = plannedSets;}

    public int getTargetReps() {return targetReps;}
    public void setTargetReps(int targetReps) {this.targetReps = targetReps;}

    public double getSuggestWeight() {return suggestWeight;}
    public void setSuggestWeight(double suggestWeight) {this.suggestWeight = suggestWeight;}

    public int getRestMinutes() {return restMinutes;}
    public void setRestMinutes (int restMinutes) {this.restMinutes = restMinutes;}

    public String getNotes(){return notes;}
    public void SetNotes(String notes){this.notes = notes;}

    public int getWarmupSets() {return warmupSets;}
    public void setWarmupSets(int warmupSets) {this.warmupSets = warmupSets;}

    public int getWorkingSets() {return workingSets;}
    public void setWorkingSets(int workingSets) {this.workingSets = workingSets;}

    public boolean isCompleted() {return isCompleted;}
    public void setCompleted(boolean completed) {this.isCompleted = completed;}


    @Override
    public String toString() {
        if (exercise == null){
            return "Planned Exercise (no exercise set)";
        }
        return exercise.getName() + ": " + workingSets + " sets x " + targetReps + " reps";
    }

}