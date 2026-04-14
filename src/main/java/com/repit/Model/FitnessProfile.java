package com.repit.Model;

public class FitnessProfile {
    private int profileId;
    private int userId;
    private double weight;
    private double height;
    private int daysPerWeek;
    private double minsAvailablePerWorkout;

    public enum FitnessLevel { BEG, INT, ADV }
    public enum FitnessGoal { MAINTAIN, BUILD, MUSCLE }

    private FitnessLevel level;
    private FitnessGoal goal;

    // Constructor
    public FitnessProfile(int userId, double weight, double height, int daysPerWeek, double minsAvailablePerWorkout, FitnessLevel level, FitnessGoal goal) {
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.daysPerWeek = daysPerWeek;
        this.minsAvailablePerWorkout = minsAvailablePerWorkout;
        this.level = level;
        this.goal = goal;
    }

    public FitnessProfile(int profileId, int userId, double weight, double height, int daysPerWeek, double minsAvailablePerWorkout, FitnessLevel level, FitnessGoal goal) {
        this.profileId = profileId;
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.daysPerWeek = daysPerWeek;
        this.minsAvailablePerWorkout = minsAvailablePerWorkout;
        this.level = level;
        this.goal = goal;
    }

    public FitnessProfile(int profileId, int userId, double weight, double height, int daysPerWeek, double minsAvailablePerWorkout, int level, int goal) {
        this.profileId = profileId;
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        this.daysPerWeek = daysPerWeek;
        this.minsAvailablePerWorkout = minsAvailablePerWorkout;
        this.level = FitnessLevel.values()[level];
        this.goal = FitnessGoal.values()[goal];
    }

    // Getters
    public int getUserId() { return userId; }
    public int getProfileId() { return profileId;}
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public int getDaysPerWeek() { return daysPerWeek; }
    public double getMinsAvailablePerWorkout() { return minsAvailablePerWorkout; }
    public FitnessLevel getLevel() { return level; }
    public FitnessGoal getGoal() { return goal;}
    public int getLevelByInt() { return level.ordinal(); }
    public int getGoalByInt() { return goal.ordinal(); }

    // Setters
    public  void setUserId(int userId) { this.userId = userId; }
    public void setWeight(float weight) { this.weight = weight; }
    public void setHeight(float height) { this.height = height; }
    public void setDaysPerWeek(int daysPerWeek) { this.daysPerWeek = daysPerWeek; }
    public void setMinsAvailablePerWorkout(float minsAvailablePerWorkout) { this.minsAvailablePerWorkout = minsAvailablePerWorkout; }
    public void setLevel(FitnessLevel level) { this.level = level; }
    public void setGoal(FitnessGoal goal) { this.goal = goal; }

    // toString
    @Override
    public String toString() {
        return "FitnessProfile{" +
                "userId=" + userId +
                ", weight=" + weight +
                ", height=" + height +
                ", daysPerWeek=" + daysPerWeek +
                ", minsAvailablePerWorkout=" + minsAvailablePerWorkout +
                ", level=" + level +
                ", goal=" + goal +
                '}';
    }
}
