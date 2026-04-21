package com.repit.Services;

import com.repit.DAOs.*;
// import com.repit.DAOs.Workout.DAO

public class ServiceDispatcher {

    // services
    // this is where we hold the services here instead of the controllers
    // are just the presentation layer but should only know about the dispatcher
    // not individual services

    private final UserService userService;
    private final WorkoutService workoutService;
    private final Exerciseservice exerciseService;
    private final FitnessProfileService fitnessProfileService;
    // private final PLannerService plannerService;



    /*
     * the constructor builds all DAOs and write them into their services.
     * controllers only ever call new serviceDispatcher()
     * they never see the dao
     */

    public ServiceDispatcher () {

        // build DAOs
        UsersDAO usersDAO = new UsersDAO();
        WorkoutLogsDAO workoutLogsDAO = new WorkoutLogsDAO();
        ExercisesDAO exercisesDAO = new ExercisesDAO();
        FitnessProfileDAO fitnessDAO = new FitnessProfileDAO();
        // WorkoutDAO workoutDAO = new WorkoutDAO();

        // wire each DAO into its matching service
        this.userService = new UserService(usersDAO);
        this.workoutService = new WorkoutService(workoutLogsDAO);
        this.exerciseService = new ExerciseService(exercisesDAO);
        this.fitnessProfileService = new FitnessProfileService(fitnessDAO);
        // this.plannerService = new PlannerService (exerciseDAO, workoutDAO);
;    }

    // user handlers

    /*
     * handleREgisterRequest
     * called by SetupController when a new user signs up
     *
     */
    public boolean handleRegisterRequest (String username, String password, String firstName,
                                          String lastName, String dateOfBirth) {
        return userService.registerUser (
                username, password, firstName, lastName, dateOfBirth);
    }

    /*
     * handle LoginRequest
     * is called by the login controller to verify credentials and returns the full user object
     * so the controllers can store the userId for the sessions
     */

    public com.repit.Model.User handleLoginRequest(String username, String password) {
        return userService.loginUser(username,password);
    }

    // workout handlers

    /*
     *  handleGetLogsRequest
     *  called by Workout Controller to load the user's logs
     */

    public java.util.ArrayList<com.repit.Model.WorkoutLog> handleGetLogsRequest (int userId) {
        return workoutService.getWorkoutLogs(userId);
    }

    /*
     * handleSaveLogsRequest
     * Called by WorkoutControllers when a set is completed
     */

    public boolean handleSaveLogRequest(com.repit.Model.WorkoutLog log) {
        return workoutService.saveWorkoutLog(log);
    }

    // exercise handlers

    /*
     *  handleGetExerciseRequest
     * called by WorkoutControllers to populate the exercise list
     */

    public java.util.ArrayList<com.repit.Model.Exercise> handleGetExercisesRequest (int userId) {
        return exerciseService.getExercises(userId);_
    }

    /*
     * handleGetSwapCandidatesRequest
     * called when the user wants to swap an exercise
     * returns only the approved replacement candidates for that exercise
     * but never the full exercise list
     */

    public java.util.ArrayList<com.repit.Model.Exercise> handleGetSwapCandidatesRequest(int exerciseId) {
        return exerciseService.getSwapCandidates(exerciseId);
    }

    /*
     * handleSwapExerciseRequest
     * called when the user confirm a swap and replaces the original exercise with
     * the chosen candidate for that user's plan only and does not affect other users
     */

    public boolean handleSwapExercisesRequest (int originalId, int replacementId, int userId) {
        return exerciseService.swapExercise (originalId,replacementId, userId);
    }

    // fitness profile handlers

    /*
     *  handleSaveProfileRequest
     * called by SetupController after the user completes setup.
     * we also call the planner service, saving the profile and generating the plan
     * are one action from the users perspective
     */

    public boolean handleSaveProfileRequest (com.repit.Model.FitnessProfile profile) {
        boolean saved = fitnessProfileService.saveProfile(profile);
        // to-do: once workout dao is ready this is to generate and save the plan immediately
        // after the profile is saved


        return saved;
    }

    /*
     * handle getProfileRequest
     * called to check if the user has completed setup
     * if this returns null the controller will redirect to setup
     */

    public com.repit.Model.FitnessProfile handleGetFitnessProfileRequest (int userId) {
        return fitnessProfileService.getProfile(userId);
    }

    /*
     * handle updateProfileRequest
     * called when the user edits their fitness preferences
     * we would also regenerate the plan here
     * if the user changes their available days or goals, their existing plan
     * is stale and need to be regenerated
     */

    public boolean handleUpdateProfileRequest (com.repit.Model.FitnessProfile profile) {
        boolean updated = fitnessProfileService.updateProfile (profile);

        // to-do  update the fitness profile

        return updated;
    }

    /*
     * to-do
     * make some of the planner handlers but after workout dao is ready
     */


}
