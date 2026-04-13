package com.repit.Model;

public class WorkoutLog {
    private int logId;
    private int userId;    // <-- links this log to a user
    private int exerciseId;
    private boolean isCompleted = false;
    private String date;

    public int getLogId(){ return logId; }
    public int getUserId(){ return userId;}
    public int getExerciseId() { return exerciseId; }
    public int getCompletion() { return isCompleted ? 1 : 0; }
    public String getDate() { return date; }

    public WorkoutLog(int plogId, int pUserId, int pExerciseId, boolean pIsCompleted,
                      String date)
    {
        userId = pUserId;
        exerciseId = pExerciseId;
        isCompleted = pIsCompleted;
    }
}
