package com.repit.Model;

public class Exercise {
    private int id;
    private String name;
    private int sets;
    private int reps;
    private int muscleGroup;
    private String difficulty;

    public int getId() { return id; }
    public String getName() { return name; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public int getMuscleGroup() { return muscleGroup; }
    public String getDifficulty() { return difficulty; }

    public Exercise(int pId, String pName, int pSets, int pReps, int pMuscleGroup, String pDifficulty){
        id = pId;
        name = pName;
        sets = pSets;
        reps = pReps;
        muscleGroup = pMuscleGroup;
        difficulty = pDifficulty;
    }
}
