package com.repit.services;

import java.time.DayOfWeek;
import java.util.*;

/**
 * Split Selector:
 * Deterimines importal workout split based on days avaibable per week
 * A split is how you divide muscle group across workout days
 *
 * Supported goals:
 *  - muscle building: focus on strength trainsing splits
 *  - weigh tloss: mix of strengh trainning and cardio
 * Examples:
 * - 3 days = upper, lower, full body
 * - 6 days = pusk, pull, legs, push, pull legs
 */
public class SplitSelector {

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
        PUSH, PULL, LEGS, UPPER, LOWER, FULL_BODY, CARDIO
    }
    // select optial workout based on avaiable days
    /**
     * @param avaibleDays is the set of days that the user can workout
     * @param goal is the user's fitness goal
     * @return is an order list of the workout type for each day
     *
     * in order for proper muscle growth, the reccomended frequency per muscle group
     * is to workout each group at least twice per week, given this
     *
     * muscle building stretgy:
     * - focus on strength training, compounds movments, progressive overload, minimal cardio
     *
     * weight loss strategy
     * - mix strength & cardio
     * - higher fequency cardio
     * - maintain muscle while losing fat
     * - more total caloreis burnes
     *
     * some examples:
     * selectSplit({Mon,WEd, Fri}), "MUSCLE BUILDING")
     * -> [upper, lower, full body]
     *
     * selectSplit({MON, WED, FRI}, "WEIGHT_LOSS")
     * -> [UPPER, LOWER, CARDIO]
     */


    public static List <WorkoutType> selectSplit (Set<DayOfWeek> availableDays, String goal){

    }

}
