package com.repit.model;

import com.repit.model.enums.*;
import com.repit.model.Equipment;
import java.util.ArrayList;
import java.util.List;

/**
 * How does it differ to planned exercise:
 * Exercise is like : What is a bench press? and Planned Exercises is like: do Bench press
 * for 3 sets of 10 for 100lbs on a Monday
 */

public class Exercise {

    // exercise identification
    private String exerciseID;
    private String name;

    // exercise classification
    private ExerciseCategory category;
    private DifficultyLevel difficulty;
    private ExerciseType exerciseType;

    // the following variables are used for calculating an exercise's priority score
    private int compoundScore;
    // help identify the muscles used in a workout
    private List<String> primaryMuscles;
    private List<String> secondaryMuscles;

    // what are some required equipment
    private List<String> requiredEquipment;

    // the following variables are used for ui variables
    //private String instructions; // how to do it
    //private String formTips; // tips for form
    // private String commonMistakes;
    //private String videoURL; // url

    // tracking
    private TrackingType trackingType;
    private boolean isCustom; // is the exercise user defined?
    private String userID;

    // default constructor
    public Exercise (){
        this.primaryMuscles = new ArrayList<>();
        this.secondaryMuscles = new ArrayList<>();
        this.requiredEquipment = new ArrayList<>();
        this.isCustom = false;
    }

    public Exercise (String name, ExerciseCategory category, DifficultyLevel difficulty, int compoundScore){
        this(); // call default constructor
        this.name = name;
        this.category = category;
        this.difficulty = difficulty;
        this.compoundScore = compoundScore;
    }

    // class methods
    public void addRequiredEquipment (String equipment) {
        if (!requiredEquipment.contains(equipment)) {
            requiredEquipment.add(equipment);
        }
    }
    public void addPrimaryMuscle (String muscle) {
        if (!primaryMuscles.contains(muscle)) {
            primaryMuscles.add(muscle);
        }
    }
    public void addSecondaryMuscle (String muscle) {
        if (!secondaryMuscles.contains(muscle)) {
            primaryMuscles.add(muscle);
        }
    }

    // does an exercise require equipment
    public boolean requiresEquipment (String equipmentID) {
        for (String e: requiredEquipment) {
            if (e.getEquipmentID() == equipmentID){}
      `       return true;
        }
        return false;
    }
    // is this exercise a bodyweight exercise

    public boolean isBodyWeight() {
        if (requiredEquipment.isEmpty()){
            return true;
        }
        /*
        if (requiredEquipment.size() == 1 ){ // idek anymore
            return requiredEquipment.get(0).equals("none");
        }
        */
        return false;
}
