package com.repit.Model;
import com.repit.Model.enums.*;
import java.util.ArrayList;
import java.util.List;

/*
 * How does it differ from PlannedExercise?
 * - Exercise: "What is a bench press?" (the definition/template)
 * - PlannedExercise: "Do bench press for 3 sets of 10 reps at 100lbs on Monday" (the instance)
 */
public class Exercise {
    private int exerciseId;
    private String name;
    private MuscleGroup muscle;
    private DifficultyLevel difficulty;
    private ExerciseType exerciseType;
    private int compoundScore;
    private boolean isCompound;
    private List<TargetedMuscle> primaryMuscles;
    private List<TargetedMuscle> secondaryMuscles;
    private List<Equipment> requiredEquipment;
    private TrackingType trackingType;
    private boolean isCustom;
    private int userId = -1;

    public Exercise() {
        this.primaryMuscles = new ArrayList<>();
        this.secondaryMuscles = new ArrayList<>();
        this.requiredEquipment = new ArrayList<>();
        this.isCustom = false;
        this.isCompound = false;
    }

    public Exercise(String name, MuscleGroup muscle,
                    DifficultyLevel difficulty, int compoundScore) {
        this();
        this.name = name;
        this.muscle = muscle;
        this.difficulty = difficulty;
        this.compoundScore = compoundScore;
    }

    public Exercise(int exerciseId, String name, MuscleGroup muscle,
                    DifficultyLevel difficulty, ExerciseType exerciseType,
                    int compoundScore, List<TargetedMuscle> primaryMuscles,
                    List<TargetedMuscle> secondaryMuscles, List<Equipment> requiredEquipment,
                    TrackingType trackingType, boolean isCustom, int userId) {
        this.exerciseId = exerciseId;
        this.name = name;
        this.muscle = muscle;
        this.difficulty = difficulty;
        this.exerciseType = exerciseType;
        this.compoundScore = compoundScore;
        this.primaryMuscles = primaryMuscles;
        this.secondaryMuscles = secondaryMuscles;
        this.requiredEquipment = requiredEquipment;
        this.trackingType = trackingType;
        this.isCustom = isCustom;
        this.userId = userId;
    }

    public void addRequiredEquipment(Equipment equipment) {
        if (!requiredEquipment.contains(equipment)) {
            requiredEquipment.add(equipment);
        }
    }

    public void addPrimaryMuscle(TargetedMuscle muscle) {
        if (!primaryMuscles.contains(muscle)) {
            primaryMuscles.add(muscle);
        }
    }

    public void addSecondaryMuscle(TargetedMuscle muscle) {
        if (!secondaryMuscles.contains(muscle)) {
            secondaryMuscles.add(muscle);
        }
    }

    public boolean requiresEquipment(int equipmentId) {
        for (Equipment eq : requiredEquipment) {
            if (eq.getEquipmentID() == equipmentId) {
                return true;
            }
        }
        return false;
    }

    public boolean isBodyweight() {
        if (requiredEquipment.isEmpty()) {
            return true;
        }
        if (requiredEquipment.size() == 1) {
            return requiredEquipment.getFirst().getEquipmentID() == -1;
        }
        return false;
    }

    public int getExerciseId() {return exerciseId;}
    public void setExerciseId(int exerciseId) {this.exerciseId = exerciseId;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public MuscleGroup getMuscle() {return muscle;}
    public int getMuscleOrdinal() {return muscle.ordinal();}
    public void setMuscle(MuscleGroup muscle) {this.muscle = muscle;}

    public DifficultyLevel getDifficulty() {return difficulty;}
    public int getDifficultyOrdinal(){return difficulty.ordinal();}
    public void setDifficulty(DifficultyLevel difficulty) {this.difficulty = difficulty;}

    public ExerciseType getExerciseType() {return exerciseType;}
    public int getExerciseTypeOrdinal() {return exerciseType.ordinal();}
    public void setExerciseType(ExerciseType type) {this.exerciseType = type;}

    public int getCompoundScore() {return compoundScore;}
    public void setCompoundScore(int score) {
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException("Compound score must be 1-10, got: " + score);
        }
        this.compoundScore = score;
    }

    public boolean isCompound() {return isCompound;}
    public void setCompound(boolean isCompound) {this.isCompound = isCompound;}

    public List<TargetedMuscle> getPrimaryMuscles() {return primaryMuscles;}
    public void setPrimaryMuscles(List<TargetedMuscle> muscles) {this.primaryMuscles = muscles;}

    public List<TargetedMuscle> getSecondaryMuscles() {return secondaryMuscles;}
    public void setSecondaryMuscles(List<TargetedMuscle> muscles) {this.secondaryMuscles = muscles;}

    public List<Equipment> getRequiredEquipment() {return requiredEquipment;}
    public void setRequiredEquipment(List<Equipment> equipment) {this.requiredEquipment = equipment;}

    public TrackingType getTrackingType() {return trackingType;}
    public int getTrackingTypeOrdinal() {return trackingType.ordinal();}
    public void setTrackingType(TrackingType type) {this.trackingType = type;}

    public boolean isCustom() {return isCustom;}
    public int isCustomOrdinal() {return isCustom ? 1:0;}
    public void setCustom(boolean custom) {this.isCustom = custom;}

    public int getUserId() {return userId;}

    @Override
    public String toString() {
        return name + " (" + muscle + ", " + difficulty + ", compound: " + compoundScore + "/10)";
    }
}