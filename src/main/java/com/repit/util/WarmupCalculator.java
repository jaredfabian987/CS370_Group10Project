package com.repit.util;

import java.util.Arrays;
import java.util.List;

/**
 * Warmup Calculator calculates warmup sets for a given working weight
 *
 * Warmup sets matter because jumping straight into your working weight increases risk of injury
 * Warmup sets prim the muscle and nervous system for heavier loads
 *
 * Protocol for weighted exercises
 * - is always the same and always has exactly 2 warm up sets
 * - set 1 is 50% of the working weight
 * - set 2 is 75% of the working weight
 *
 * Protocol for bodyweight exercises:
 * - also always exactly 2 warm up sets
 * - set 1: 50% of working reps
 * - set 2: 75% of working reps
 * - both are going to be rounded to the nearest whole rep
 *
 * Protocol for calibration week:
 * - no warm up sets during the first week
 * - user performs straight working sets to establish a baseline weight for each exercise
 * - once the calibration week is complete the warm up sets resume normally
 *
 * some examples:
 * if working weight = 225lbs
 * set 1 = 11lbs (225 * 0.5 = 112.5 gets rounded to 115)
 * set 2 = 170lbs (225 * 0.75 = 168.75 but rounds to 170)
 *
 * if working bodyweight reps = 10 pullups
 * set 1 = 5 reps since 10 * 0.50 = 5
 * set 2 = 8 since 10 * 0.75 = 7.5 but rounded is 8
 *
 * Some considerations
 * - Why do we round to the nearest 5lbs?
 * - Most of the gyms that I have been to only have 5lbs increments in their machines
 * - and in free weight plates or dumbbells
 *
 *
 */
public class WarmupCalculator {

    // as mention above this number is not just something made up
    // this value will help calculate the user's warm up weight by giving us a
    // constant value to round to

    private static final double ROUND_TO = 5.0;

    // these two values are to help us calculate warm up weight
    // they are going to be our multipliers

    private static final double SET1_MULTIPLIER = 0.50;
    private static final double SET2_MULTIPLIER = 0.75;

    /**
     * calculateWarmups
     * returns an ordered list of two warm up weights for a given working weight
     *
     * We return a list and not separate values because I thought it would make the method
     * a little more clean and makes it easier for WorkoutGenerator to iterate over the warmup sets
     * the same way it iterates over working sets
     *
     * @param workingWeight is the weight the user will lift for their working sets in lbs
     * @return ordered list of two warmup weight in lbs, ex: [set1,set2] both rounded to the nearest 5lbs
     * @throws IllegalArgumentException if working weight is zero or negative
     *
     * Examples:
     * calculateWarmups(225.0) expects -> [115.0,170.0]
     * calculateWarmup(135.0) expects -> [70.0,100.0]
     */



    public static List<Double> calculateWarmups(double workingWeight) {

        // first we validate input of working weight
        // 0 or negative values makes no sense and would produce bad values so we proceed with caution

        if (workingWeight <= 0) {
            throw new IllegalArgumentException("workingWeight must be a positive value, got: " + workingWeight);
        }

        // calculate the raw percentages first before rounding
        double rawSet1 = workingWeight * SET1_MULTIPLIER;
        double rawSet2 = workingWeight * SET2_MULTIPLIER;

        // now we round each to the nearest 5lbs

        double set1 = roundToNearest(rawSet1, ROUND_TO);
        double set2 = roundToNearest(rawSet2, ROUND_TO);

        return Arrays.asList(set1, set2);
    }
    /**
     * isCalibrationWeek
     * Checks whether the user has completed their calibration week yet.
     * If they have not, warm up sets should be entirely and WorkoutGenerator
     * should only assign working sets so the user can establish a baseline weight
     *
     * @param isCalibrated the calibration flag from FitnessProfile
     * @return true if the user is still in calibration week, false if they have completed it and warmups only
     *
     * Example:
     * isCalibrationWeek(false) = true (not yet calibrated)
     * isCalibrationWeek(true) = false (calibrated, use warmups)
     */

    public static boolean isCalibrationWeek(boolean isCalibrated){
        // we invert is calibrated because true means that they finished calibration
        // so the calibration week is active when isCalibrated = false

        return !isCalibrated;
    }

    /**
     * calculateWarmupsBodyweight
     * if the exercise is a bodyweight movement then this method will be called
     *
     * int this case we use reps instead of weight for bodyweight movements like:
     * pullups, dips, and push-ups that have no external load to reduce so we warm up by doing
     * a fraction of the working resp instead of a fraction of the working weight
     * in this function we are also returning a List<interger> and not List<double>
     * since you cannot return half of a rep
     *
     * @param workingReps is the number of reps the user will do for their working sets
     * @return is an ordered list of two warm up rep counts, [set1,set2] both of which
     * are going to be rounded to the nearest whole rep
     * @throws IllegalArgumentException if the workingReps is 0 or less
     *
     */

    public static List<Integer> calculateWarmupsBodyweight (int workingReps){

        // again first we validate before proceeding - zero or negative reps
        // makes no sense and would produce bad warmup values
        if (workingReps <= 0) {
            throw new IllegalArgumentException("workingReps must be a positive value, got: " + workingReps);
        }

        // calculate the raw percentages first before rounding
        double rawSet1 = workingReps * SET1_MULTIPLIER;
        double rawSet2 = workingReps * SET2_MULTIPLIER;

        // round to the nearest whole rep using Math.round
        int set1 = (int) Math.round(rawSet1);
        int set2 = (int) Math.round(rawSet2);


        return Arrays.asList(set1,set2);
    }


    /**
     * roundToNearest
     * Needed to make my own round to nearest function because apparently java doesn't
     * have a round function where you can round to the nearest value/increment
     *
     * Rounds ta value to the nearest multiple of a given increment
     *
     * Private because we only want to use it within Calculate Warmups class
     *
     * how the math works:
     * 1) divide the value by the increment for example 112.5 / 5.0 = 22.5
     * 2) round to th nearest whole number (Math.round (22.5) = 23)
     * 3) then we multiply back by the increment 23 (23 * 5.0 11.50)
     *
     * @param value the raw value to round
     * @param increment the multiple to round to
     * @return the value rounded to the nearest increment
     * @author claude sonnet 4.6 using prompt: can you help me write a function that can help me round a value
     * to the nearest increment because I can't find that comes built into java 04/21/2026 6:11pm
     * - prompted by Jared Fabian
     *
     */

    private static double roundToNearest (double value, double increment) {
        return Math.round(value / increment) * increment;
    }
}
