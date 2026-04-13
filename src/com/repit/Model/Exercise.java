package com.repit.Model;

public class Exercise {
    private int id;
    private int userId; //REMOVE USERS
    private String name;
    private int sets;
    private int reps;
    private String muscleGroup;
    private String difficulty;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public String getMuscleGroup() { return muscleGroup; }
    public String getDifficulty() { return difficulty; }

    public Exercise(int pId, int pUserId,String pName, int pSets,
                    int pReps, String pMuscleGroup, String pDifficulty){
        userId = pUserId;
        name = pName;
        sets = pSets;
        reps = pReps;
        muscleGroup = pMuscleGroup;
        difficulty = pDifficulty;
    }
}
