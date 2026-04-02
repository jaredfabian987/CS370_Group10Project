package com.repit.model;


/**
 * This is the model layer wih just data structures
 *
 *
 * Represent a planned exercise in a workout
 *
 * Example:
 * - Exercise: Bench Press
 * - Sets: 3 (2 Warmup + 2- working)
 * - Reps: 10
 * - Weight: 60Kg
 *
 */
public class PlannedExercise {

    private String plannedExerciseId;
    private Exercise exercise;      // referenced to acual workout
    private int ExerciseOrder;      // where it places in the workout, 1st, 2nd, ...
    private int plannedSets;        // total amount of sets both working and warmup
    private int targetReps;         // target reps for a given set
    private double suggestWeight;   // starting weight in lbs
    private int restMinutes;            // rest between sets
    private String notes;           // ex: "Need to work on form..."

    // for the warmup protocol
    private int warmupSets;         // number of warmup sets which is usually 2
    private int workingSets;        // number of working sets 2-3

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
    public String plannedExerciseId() {
        return plannedExerciseId;
    }
    public void setPlannedExerciseId(String plannedExerciseId) {
        this.plannedExerciseId = plannedExerciseId;
    }

    public Exercise getExercise() {
        return exercise;
    }
    public void setExercise (Exercise exercise) {
        this.exercise = exercise;
    }

    public int getPlannedSets() {
        return plannedSets;
    }
    public void setPlannedSets(int plannedSets) {
        this.plannedSets = plannedSets;
    }

    public int getTargetReps() {
        return targetReps;
    }
    public void setTargetReps(int targetReps) {
        this.targetReps = targetReps;
    }

    public double getSuggestWeight() {
        return suggestWeight;
    }
    public void setSuggestWeight(double suggestWeight) {
        this.suggestWeight = suggestWeight;
    }

    public int getRestMinutes() {
        return restMinutes;
    }
    public void setRestMinutes (int restMinutes) {
        this.restMinutes = restMinutes;
    }

    public String getNotes(){
        return notes;
    }
    public void SetNotes(String notes){
        this.notes = notes;
    }

    public int getWarmupSets() {
        return warmupSets;
    }
    public void setWarmupSets(int warmupSets) {
        this.warmupSets = warmupSets;
    }
    public int getWorkingSets() {
        return workingSets;
    }
    public void setWorkingSets(int workingSets) {
        this.workingSets = workingSets;
    }

    /*&
    @Override
    public String toString() {
        if (exercise == null){
            return "Planned Exercise (no exercise set)";
        }
        return exercise.getName() + ": " + workingSets + " sets x " + targetReps + " reps";
    }
    */
}
