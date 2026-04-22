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
    // id's
    private int exerciseId;
    private String name;
    // enums
    private ExerciseCategory category;
    private DifficultyLevel difficulty;
    private ExerciseType exerciseType;

    // compounds score for heap
    private int compoundScore;

    // Help identify the muscles used in a workout
    private List<TargetedMuscle> primaryMuscles;
    private List<TargetedMuscle> secondaryMuscles;

    // equipment
    private List<Equipment> requiredEquipment;

    // tracking
    private TrackingType trackingType;  // REPS_AND_WEIGHT, TIME, DISTANCE, etc.
    private boolean isCustom;           // User-defined exercise?
    private int userId = -1;              // If custom, who created it?

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

    public Exercise(int exerciseId, String name, ExerciseCategory category,
                    DifficultyLevel difficulty, ExerciseType exerciseType,
                    int compoundScore, List<TargetedMuscle> primaryMuscles,
                    List<TargetedMuscle> secondaryMuscles, List<Equipment> requiredEquipment,
                    TrackingType trackingType, boolean isCustom, int userId) {

        this.exerciseId = exerciseId;
        this.name = name;
        this.category = category;
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

    // methods
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
            secondaryMuscles.add(muscle);  // ← Fixed! Was primaryMuscles
        }
    }

    /*

     */
    public boolean requiresEquipment(int equipmentId) {
        for (Equipment eq : requiredEquipment) {
            if (eq.getEquipmentID() == equipmentId) {  // ← Fixed!
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
            return requiredEquipment.getFirst().getEquipmentID() == -1;
        }
        return false;
    }

    public int getExerciseId() {return exerciseId;}

    public void setExerciseId(int exerciseId) {this.exerciseId = exerciseId;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public ExerciseCategory getCategory() {return category;}
    public int getCategoryOrdinal() {return category.ordinal();}

    public void setCategory(ExerciseCategory category) {this.category = category;}

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

   // public void setUserId(String userId) {this.userId = userId;}

    // toString() - For debugging
    @Override
    public String toString() {
        return name + " (" + category + ", " + difficulty + ", compound: " + compoundScore + "/10)";
    }
}