package com.repit.Model;

public class WorkoutLog {
    private int logId;
    private int userId;
    private int exerciseId;
    private boolean isCompleted = false;
    private String date;
    private int reps;
    private double weight;

    public int getLogId(){ return logId; }
    public int getUserId(){ return userId;}
    public int getExerciseId() { return exerciseId; }
    public int getCompletion() { return isCompleted ? 1 : 0; }
    public boolean isCompleted() { return isCompleted; }
    public String getDate() { return date; }
    public int getReps() { return reps; }
    public double getWeight() { return weight; }

    public void setReps(int reps) { this.reps = reps; }
    public void setWeight(double weight) { this.weight = weight; }

    public WorkoutLog(int plogId, int pUserId, int pExerciseId, boolean pIsCompleted,
                      String date)
    {
        logId = plogId;
        userId = pUserId;
        exerciseId = pExerciseId;
        isCompleted = pIsCompleted;
        this.date = date;
    }

    public WorkoutLog(int plogId, int pUserId, int pExerciseId, boolean pIsCompleted,
                      String date, int reps, double weight)
    {
        this(plogId, pUserId, pExerciseId, pIsCompleted, date);
        this.reps = reps;
        this.weight = weight;
    }
}
