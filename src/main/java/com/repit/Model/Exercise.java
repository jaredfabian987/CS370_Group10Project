package com.repit.Model;

import com.repit.model.enums.*;
import java.util.ArrayList;
import java.util.List;

/*
 * How does it differ from PlannedExercise?
 * - Exercise: "What is a bench press?" (the definition/template)
 * - PlannedExercise: "Do bench press for 3 sets of 10 reps at 100lbs on Monday" (the instance)
 */
public class Exercise {
    // id's
    private String exerciseId;
    private String name;
    // enums
    private ExerciseCategory category;
    private DifficultyLevel difficulty;
    private ExerciseType exerciseType;

    // compounds score for heap
    private int compoundScore;

    // Help identify the muscles used in a workout
    private List<String> primaryMuscles;
    private List<String> secondaryMuscles;

    // equipment
    private List<Equipment> requiredEquipment;

    // tracking
    private TrackingType trackingType;  // REPS_AND_WEIGHT, TIME, DISTANCE, etc.
    private boolean isCustom;           // User-defined exercise?
    private String userId;              // If custom, who created it?

    // construcots
    public Exercise() {
        this.primaryMuscles = new ArrayList<>();
        this.secondaryMuscles = new ArrayList<>();
        this.requiredEquipment = new ArrayList<>();
        this.isCustom = false;
    }

    public Exercise(String name, ExerciseCategory category,
                    DifficultyLevel difficulty, int compoundScore) {
        this();  // Call default constructor first
        this.name = name;
        this.category = category;
        this.difficulty = difficulty;
        this.compoundScore = compoundScore;
    }

    // methods
    public void addRequiredEquipment(Equipment equipment) {
        if (!requiredEquipment.contains(equipment)) {
            requiredEquipment.add(equipment);
        }
    }


    public void addPrimaryMuscle(String muscle) {
        if (!primaryMuscles.contains(muscle)) {
            primaryMuscles.add(muscle);
        }
    }


    public void addSecondaryMuscle(String muscle) {
        if (!secondaryMuscles.contains(muscle)) {
            secondaryMuscles.add(muscle);  // ← Fixed! Was primaryMuscles
        }
    }

    /*

     */
    public boolean requiresEquipment(String equipmentId) {
        for (Equipment eq : requiredEquipment) {
            if (eq.getEquipmentID().equals(equipmentId)) {  // ← Fixed!
                return true;
            }
        }
        return false;
    }

   // is it a bodyweight movement
    public boolean isBodyweight() {
        if (requiredEquipment.isEmpty()) {
            return true;
        }
        if (requiredEquipment.size() == 1) {
            return requiredEquipment.getFirst().getEquipmentID().equals("none");
        }
        return false;
    }



    public String getExerciseId() {return exerciseId;}

    public void setExerciseId(String exerciseId) {this.exerciseId = exerciseId;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public ExerciseCategory getCategory() {return category;}

    public void setCategory(ExerciseCategory category) {this.category = category;}

    public DifficultyLevel getDifficulty() {return difficulty;}

    public void setDifficulty(DifficultyLevel difficulty) {this.difficulty = difficulty;}

    public ExerciseType getExerciseType() {return exerciseType;}

    public void setExerciseType(ExerciseType type) {this.exerciseType = type;}
    public int getCompoundScore() {return compoundScore;}

    public void setCompoundScore(int score) {
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException("Compound score must be 1-10, got: " + score);
        }
        this.compoundScore = score;
    }

    public List<String> getPrimaryMuscles() {return primaryMuscles;}

    public void setPrimaryMuscles(List<String> muscles) {this.primaryMuscles = muscles;}

    public List<String> getSecondaryMuscles() {return secondaryMuscles;}

    public void setSecondaryMuscles(List<String> muscles) {this.secondaryMuscles = muscles;}

    public List<Equipment> getRequiredEquipment() {return requiredEquipment;}

    public void setRequiredEquipment(List<Equipment> equipment) {this.requiredEquipment = equipment;}

    public TrackingType getTrackingType() {return trackingType;}

    public void setTrackingType(TrackingType type) {this.trackingType = type;}

    public boolean isCustom() {return isCustom;}

    public void setCustom(boolean custom) {this.isCustom = custom;}

    public String getUserId() {return userId;}

    public void setUserId(String userId) {this.userId = userId;}

    // toString() - For debugging
    @Override
    public String toString() {
        return name + " (" + category + ", " + difficulty + ", compound: " + compoundScore + "/10)";
    }
}