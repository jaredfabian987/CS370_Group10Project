package com.repit.Services;

import com.repit.DAOs.ExercisesDAO;
import com.repit.DAOs.FitnessProfileDAO;
import com.repit.DAOs.UsersDAO;
import com.repit.DAOs.WorkoutLogsDAO;
import com.repit.Model.Exercise;
import com.repit.Model.FitnessProfile;
import com.repit.Model.User;
import com.repit.Model.ProgressionSuggestion;
import com.repit.Model.WorkoutLog;
import com.repit.Model.enums.WorkoutType;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ServiceDispatcher {

    // services
    // this is where we hold the services here instead of the controllers
    // controllers are just the presentation layer but should only know about the dispatcher
    // not individual services

    private final UserService userService;
    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final ProgressService progressService;
    private final DashboardService dashboardService;
    private final FitnessProfileService fitnessProfileService;

    /*
     * the constructor builds all DAOs and writes them into their services.
     * controllers only ever call new ServiceDispatcher()
     * they never see the dao
     */
    public ServiceDispatcher() {

        // build DAOs
        UsersDAO usersDAO               = new UsersDAO();
        WorkoutLogsDAO workoutLogsDAO   = new WorkoutLogsDAO();
        ExercisesDAO exercisesDAO       = new ExercisesDAO();
        FitnessProfileDAO fitnessDAO    = new FitnessProfileDAO();

        // wire each DAO into its matching service
        this.userService            = new UserService(usersDAO);
        this.workoutService         = new WorkoutService(workoutLogsDAO);
        this.exerciseService        = new ExerciseService(exercisesDAO);
        this.progressService        = new ProgressService(workoutLogsDAO);
        this.dashboardService       = new DashboardService(workoutLogsDAO, fitnessDAO);
        this.fitnessProfileService  = new FitnessProfileService(fitnessDAO);
    }

    // user handlers

    /*
     * handleRegisterRequest
     * called by SetupController when a new user signs up
     */
    public boolean handleRegisterRequest(String username, String password,
                                         String firstName, String lastName,
                                         String dateOfBirth) {
        return userService.registerUser(username, password, firstName, lastName, dateOfBirth);
    }

    /*
     * handleLoginRequest
     * is called by the login controller to verify credentials and returns the full user object
     * so the controllers can store the userId for the session
     */
    public User handleLoginRequest(String username, String password) {
        return userService.loginUser(username, password);
    }

    // workout handlers

    /*
     * handleGetLogsRequest
     * called by WorkoutController to load the user's logs
     */
    public ArrayList<WorkoutLog> handleGetLogsRequest(int userId) {
        return workoutService.getWorkoutLogs(userId);
    }

    /*
     * handleSaveLogRequest
     * called by WorkoutController when a set is completed
     */
    public boolean handleSaveLogRequest(WorkoutLog log) {
        return workoutService.saveWorkoutLog(log);
    }

    /*
     * handleGetSplitRequest
     * called by WorkoutController or PlannerController to determine the user's
     * workout split based on their available training days and goal
     */
    public List<WorkoutType> handleGetSplitRequest(Set<DayOfWeek> availableDays, String goal) {
        return workoutService.getSplit(availableDays, goal);
    }

    /*
     * handleGetWarmupsRequest
     * called by WorkoutController to get warmup weights for a weighted exercise
     * returns [set1Weight, set2Weight] rounded to the nearest 5lbs
     */
    public List<Double> handleGetWarmupsRequest(double workingWeight) {
        return workoutService.getWarmups(workingWeight);
    }

    /*
     * handleGetWarmupsBodyweightRequest
     * called by WorkoutController to get warmup rep counts for a bodyweight exercise
     * returns [set1Reps, set2Reps] rounded to the nearest whole rep
     */
    public List<Integer> handleGetWarmupsBodyweightRequest(int workingReps) {
        return workoutService.getWarmupsBodyweight(workingReps);
    }

    // exercise handlers

    /*
     * handleGetExercisesRequest
     * called by WorkoutController to populate the exercise list
     */
    public ArrayList<Exercise> handleGetExercisesRequest(int userId) {
        return exerciseService.getExercises(userId);
    }

    /*
     * handleGetSwapCandidatesRequest
     * called when the user wants to swap an exercise
     * returns only the approved replacement candidates for that exercise
     * but never the full exercise list
     */
    public ArrayList<Exercise> handleGetSwapCandidatesRequest(int exerciseId) {
        return exerciseService.getSwapCandidates(exerciseId);
    }

    /*
     * handleSwapExerciseRequest
     * called when the user confirms a swap and replaces the original exercise with
     * the chosen candidate for that user's plan only and does not affect other users
     */
    public boolean handleSwapExercisesRequest(int originalId, int replacementId, int userId) {
        return exerciseService.swapExercise(originalId, replacementId, userId);
    }

    /*
     * handleBuildExerciseQueueRequest
     * called by WorkoutController to get a ranked, time-capped list of exercises
     * for a given workout day. compounds are always prioritized over isolations
     */
    public List<Exercise> handleBuildExerciseQueueRequest(List<Exercise> exercises,
                                                           WorkoutType workoutType,
                                                           int availableMinutes) {
        return exerciseService.buildExerciseQueue(exercises, workoutType, availableMinutes);
    }

    // progress handlers

    /*
     * handleGetProgressionSuggestionRequest
     * called by ProgressController after a set is logged to check if the user
     * has met the progressive overload threshold and should increase weight or reps
     * returns null if the user has no prior history for that exercise
     */
    public ProgressionSuggestion handleGetProgressionSuggestionRequest(int userId,
                                                                        int exerciseId,
                                                                        Exercise exercise) {
        return progressService.suggestProgression(userId, exerciseId, exercise);
    }

    // dashboard handlers

    /*
     * handleGetLastWorkoutRequest
     * called by DashboardController to show the "last workout" summary card
     * returns null if the user has not logged any workouts yet
     */
    public WorkoutLog handleGetLastWorkoutRequest(int userId) {
        return dashboardService.getLastWorkout(userId);
    }

    /*
     * handleGetWorkoutCountRequest
     * called by DashboardController to display the total workout count stat
     */
    public int handleGetWorkoutCountRequest(int userId) {
        return dashboardService.getWorkoutCount(userId);
    }

    /*
     * handleGetProfileSummaryRequest
     * called by DashboardController to populate the dashboard header
     * returns null if the user has not completed setup — controller should redirect to setup
     */
    public FitnessProfile handleGetProfileSummaryRequest(int userId) {
        return dashboardService.getProfileSummary(userId);
    }

    // fitness profile handlers

    /*
     * handleSaveProfileRequest
     * called by SetupController after the user completes setup.
     * saving the profile and generating the plan are one action from the user's perspective
     */
    public boolean handleSaveProfileRequest(FitnessProfile profile) {
        boolean saved = fitnessProfileService.saveProfile(profile);
        // to-do: once WorkoutDAO is ready, generate and save the plan immediately
        // after the profile is saved so the user goes straight into their first workout
        return saved;
    }

    /*
     * handleGetFitnessProfileRequest
     * called to check if the user has completed setup
     * if this returns null the controller will redirect to setup
     */
    public FitnessProfile handleGetFitnessProfileRequest(int userId) {
        return fitnessProfileService.getProfile(userId);
    }

    /*
     * handleUpdateProfileRequest
     * called when the user edits their fitness preferences
     * we would also regenerate the plan here
     * if the user changes their available days or goals, their existing plan
     * is stale and needs to be regenerated
     */
    public boolean handleUpdateProfileRequest(FitnessProfile profile) {
        boolean updated = fitnessProfileService.updateProfile(profile);
        // to-do: regenerate the workout plan when the profile is updated
        return updated;
    }

    // calibration handlers

    /*
     * handleCalibrationLogRequest
     * called during calibration week when the user logs the
     * weight they used for each exercise.
     * this weight becomes their baseline working weight for
     * all future warmup and working set calculations.
     */
    // the log is the WorkoutLog entry containing the baseline weight
    public boolean handleCalibrationLogRequest(WorkoutLog log) {
        return workoutService.saveWorkoutLog(log);
    }

    /*
     * handleCompleteCalibrationRequest
     * called at the end of calibration week to mark the user
     * as calibrated so WarmupCalculator starts applying warmup
     * sets from the next workout onwards.
     *
     * this is a separate handler and is not automatic because
     * the user must complete a full week of calibration before
     * we flip the flag value, we don't want to mark them calibrated after they do just one session
     */
    public boolean handleCompleteCalibrationRequest(int userId) {
        FitnessProfile profile = fitnessProfileService.getProfile(userId);
        if (profile == null) {
            throw new IllegalArgumentException(
                    "No profile found for userId: " + userId
            );
        }
        // flip the calibration flag on the profile
        // this tells WarmupCalculator to start applying
        // warmup sets from the next workout onwards
        profile.setCalibrated(true);
        // return true if the profile was updated successfully
        return fitnessProfileService.updateProfile(profile);
    }

    /*
     * to-do
     * make some of the planner handlers but after WorkoutDAO is ready
     */

}
