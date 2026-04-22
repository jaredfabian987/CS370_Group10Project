package com.repit.Model;
import  com.repit.Model.enums.MuscleRole;

public class TargetedMuscle {
    private int targetedMuscleId;
    private int exerciseId;
    private String muscle;
    private MuscleRole role;

    // Full constructor
    public TargetedMuscle(int targetedMuscleId, int exerciseId, String muscle, MuscleRole role) {
        this.targetedMuscleId = targetedMuscleId;
        this.exerciseId = exerciseId;
        this.muscle = muscle;
        this.role = role;
    }

    public TargetedMuscle(int targetedMuscleId, int exerciseId, String muscle, int roleOrdinal) {
        this.targetedMuscleId = targetedMuscleId;
        this.exerciseId = exerciseId;
        this.muscle = muscle;
        this.role = MuscleRole.values()[roleOrdinal];
    }

    // Constructor without ID (for creating new records before DB assigns an ID)
    public TargetedMuscle(int exerciseId, String muscle, MuscleRole role) {
        this.exerciseId = exerciseId;
        this.muscle = muscle;
        this.role = role;
    }

    public int getTargetedMuscleId() {
        return targetedMuscleId;
    }

    public void setTargetedMuscleId(int targetedMuscleId) {
        this.targetedMuscleId = targetedMuscleId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public MuscleRole getRole() {
        return role;
    }

    public int getRoleByInt() {
        return role.ordinal();
    }

    public void setRole(MuscleRole role) {
        this.role = role;
    }

    public void setRoleByInt(int roleOrdinal) {
        this.role = MuscleRole.values()[roleOrdinal];
    }
}