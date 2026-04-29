package com.repit.Services;

import com.repit.DAOs.ExercisesDAO;
import com.repit.util.ExercisePriorityQueue;
import com.repit.Model.Exercise;
import com.repit.Model.enums.WorkoutType;

import java.util.ArrayList;
import java.util.List;

/**
 * ExerciseService
 * Handles all exercise-related business logic.
 *
 * Responsibilities:
 * - Fetching exercises from ExercisesDAO
 * - Delegating exercise ranking to ExercisePriorityQueue
 *
 * Controllers never touch ExercisesDAO or ExercisePriorityQueue directly.
 */
public class ExerciseService {

    private final ExercisesDAO exercisesDAO;
    private final ExercisePriorityQueue exercisePriorityQueue;

    public ExerciseService(ExercisesDAO exercisesDAO) {
        this.exercisesDAO = exercisesDAO;
        this.exercisePriorityQueue = new ExercisePriorityQueue();
    }

    // --- Exercise Retrieval ---

    /**
     * Returns all exercises available to a user.
     * Includes both global exercises and any custom exercises the user created.
     *
     * @param userId the logged-in user's ID
     * @return list of exercises for the user
     */
    public ArrayList<Exercise> getExercises(int userId) {
        return exercisesDAO.getExercises(userId);
    }

    // --- Exercise Swapping --- (feature removed)
    /*
    public ArrayList<Exercise> getSwapCandidates(int exerciseId) {
        return new ArrayList<>();
    }

    public boolean swapExercise(int originalId, int replacementId, int userId) {
        return false;
    }
    */

    // --- Exercise Ranking ---

    /**
     * Returns an ordered list of exercises for a given workout day,
     * ranked by relevance to the workout type and capped by available time.
     * Compounds are always prioritized over isolation movements.
     * Delegates to ExercisePriorityQueue.
     *
     * @param exercises        full list of candidate exercises
     * @param workoutType      the type of workout day (PUSH, PULL, LEGS, etc.)
     * @param availableMinutes how many minutes the user has, must be a positive multiple of 10
     * @return ordered list of exercises, compounds first then isolations
     */
    public List<Exercise> buildExerciseQueue(List<Exercise> exercises,
                                              WorkoutType workoutType,
                                              int availableMinutes) {
        return exercisePriorityQueue.buildQueue(exercises, workoutType, availableMinutes);
    }
}
