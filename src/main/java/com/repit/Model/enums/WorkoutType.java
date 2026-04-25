package com.repit.Model.enums;

/**
 * types of workouts in a split
 * push: chest, shoulders, triceps (pushing movements)
 * pull: back, biceps (pulling movements)
 * legs: quads, hamstrings, glutes, calves
 * upper: all upper body
 * lower: same as legs
 * full body = everything in on workout
 * cardio: some form of aerobic exercise day
 */
public enum WorkoutType {
    PUSH, PULL, LEGS, UPPER, LOWER, FULL_BODY, CARDIO, REST, CORE
}