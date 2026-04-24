package com.repit.Model;

public class ProgressionSuggestion {

    private final double suggestedWeight;
    private final int suggestedReps;
    private final boolean readyToProgress;

    public ProgressionSuggestion(double suggestedWeight, int suggestedReps, boolean readyToProgress) {
        this.suggestedWeight = suggestedWeight;
        this.suggestedReps = suggestedReps;
        this.readyToProgress = readyToProgress;
    }

    public double getSuggestedWeight() { return suggestedWeight; }
    public int getSuggestedReps() { return suggestedReps; }

    // true if the user hit the threshold and a weight/rep increase is suggested
    public boolean isReadyToProgress() { return readyToProgress; }
}
