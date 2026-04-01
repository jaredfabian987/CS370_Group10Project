package com.repit.Model;

public class WorkoutLog {
    private int logId;
    private int userId;    // <-- links this log to a user
    private String desc;
    private boolean isCompleted = false;

    public int getLogId(){ return logId; }
    public int getUserId(){ return userId;}
    public String getDesc() { return desc; }
    public int getCompletion() { return isCompleted ? 1 : 0; }

    public WorkoutLog(int pLogId, int pUserId, String pDesc, boolean pIsCompleted){
        logId = pLogId;
        userId = pUserId;
        desc = pDesc;
        isCompleted = pIsCompleted;
    }
}
