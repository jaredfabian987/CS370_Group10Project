package com.repit.Services;

import com.repit.DAOs.ExercisesDAO;
import com.repit.DAOs.FitnessProfileDAO;
import com.repit.DAOs.UsersDAO;
import com.repit.DAOs.WorkoutLogsDAO;
import com.repit.Model.DayWorkoutPlan;
import com.repit.Model.Exercise;
import com.repit.Model.FitnessProfile;
import com.repit.Model.User;
import com.repit.Model.ProgressionSuggestion;
import com.repit.Model.WorkoutLog;
import com.repit.Model.enums.WorkoutType;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final PlannerService plannerService;

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
        this.plannerService         = new PlannerService(fitnessDAO, exercisesDAO, workoutLogsDAO);

        // seed coaching cues on startup — safe to call every launch, only updates by name
        exercisesDAO.seedCoachingCues();
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

    // --- Exercise Swapping --- (feature removed)
    /*
    public ArrayList<Exercise> handleGetSwapCandidatesRequest(int exerciseId) {
        return exerciseService.getSwapCandidates(exerciseId);
    }

    public boolean handleSwapExercisesRequest(int originalId, int replacementId, int userId) {
        return exerciseService.swapExercise(originalId, replacementId, userId);
    }
    */

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

    /*
     * handleGetCompletedExerciseCountTodayRequest
     * called by WorkoutController or DashboardController for the "n out of n exercises"
     * counter on the workout screen.
     * returns the number of distinct exercises the user has completed today.
     * pair with handleGetTotalExercisesPlannedRequest() to build the full label:
     * e.g. "3 out of 5 exercises completed"
     */
    public int handleGetCompletedExerciseCountTodayRequest(int userId) {
        return progressService.getCompletedExerciseCountToday(userId);
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

    /*
     * handleGetWeeklyWorkoutsCompletedRequest
     * called by DashboardController to show the "n out of n workouts" progress banner.
     * returns the number of days the user has already trained this week (Mon-Sun).
     * pair this with handleGetWeeklyWorkoutsPlannedRequest() to build the full label:
     * e.g. "2 out of 4 workouts completed"
     */
    public int handleGetWeeklyWorkoutsCompletedRequest(int userId) {
        return dashboardService.getWeeklyWorkoutsCompleted(userId);
    }

    /*
     * handleGetWeeklyWorkoutsPlannedRequest
     * called by DashboardController to get the total planned training days per week.
     * comes directly from the user's fitness profile (daysPerWeek field).
     */
    public int handleGetWeeklyWorkoutsPlannedRequest(int userId) {
        return dashboardService.getWeeklyWorkoutsPlanned(userId);
    }

    /*
     * handleGetTotalExercisesPlannedRequest
     * called by DashboardController or WorkoutController for the per-session progress counter.
     * returns how many exercises fit in the user's time budget (minsAvailablePerWorkout / 10).
     * e.g. 50 min → 5 exercises, so the counter can show "3 out of 5 exercises done"
     */
    public int handleGetTotalExercisesPlannedRequest(int userId) {
        return dashboardService.getTotalExercisesPlannedPerSession(userId);
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

    // planner handlers

    /*
     * handleGetWeeklyPlanRequest
     * called by PlannerController to build the full Mon-Sun plan for the dashboard.
     * returns a map of DayOfWeek -> DayWorkoutPlan.
     * each training day has a ranked exercise list and an isCompleted flag.
     * rest days are included with isRestDay = true and an empty exercise list.
     * returns null if the user has not completed setup.
     *
     * HOW THE WORKOUT GENERATION ALGORITHM WORKS (step by step):
     *
     *  Step 1 — Load fitness profile
     *    Read the user's goal (BUILD/MUSCLE/MAINTAIN), daysPerWeek, and
     *    minsAvailablePerWorkout from FitnessProfileDAO.
     *
     *  Step 2 — Pick training days
     *    Use daysPerWeek to choose which days of the week the user trains.
     *    Default pattern: 3 days → Mon/Wed/Fri, 4 days → Mon/Tue/Thu/Fri, etc.
     *    (Will be replaced by AvailabilityDAO once built.)
     *
     *  Step 3 — Select the split
     *    Pass the training days and goal into SplitSelector.selectSplit().
     *    SplitSelector returns an ordered list of WorkoutTypes — one per training day.
     *    Example: 3-day MUSCLE_BUILDING → [UPPER, LOWER, FULL_BODY]
     *    Example: 5-day MUSCLE_BUILDING → [PUSH, PULL, LEGS, UPPER, LOWER]
     *
     *  Step 4 — Resolve the time budget
     *    Floor minsAvailablePerWorkout to the nearest 10-minute block.
     *    Each 10-minute block = 1 exercise (2 warmup + 2 working sets + rest).
     *    Example: 50 min → 5 exercise slots.
     *
     *  Step 5 — Rank exercises per day
     *    For each training day, call ExercisePriorityQueue.buildQueue() with
     *    the day's WorkoutType and the time budget.
     *    The queue scores every exercise: +2 if the primary muscle matches the day's
     *    target, +1 per secondary muscle match. Score 0 = wrong day, excluded.
     *    Compounds always fill first; isolations fill up to a max of 2 slots.
     *    Example: PUSH day, 50 min → 3 chest/shoulder compounds + 1 tricep + 1 shoulder isolation.
     *
     *  Step 6 — Wrap exercises in PlannedExercise
     *    Each ranked Exercise is wrapped in a PlannedExercise with:
     *    2 warmup sets + 2 working sets (our standard protocol).
     *    Suggested weight defaults to 0 here; the workout controller fills it
     *    in from the user's last logged weight via ProgressService.
     *
     *  Step 7 — Mark completed days
     *    Call WorkoutLogsDAO.getLoggedDatesThisWeek() to get every date this
     *    Mon-Sun week where the user logged at least one completed set.
     *    Any training day whose calendar date is in that set gets isCompleted = true.
     *
     *  Step 8 — Return the full week
     *    Return Map<DayOfWeek, DayWorkoutPlan> ordered Monday through Sunday.
     */
    public Map<DayOfWeek, DayWorkoutPlan> handleGetWeeklyPlanRequest(int userId) {
        return plannerService.getWeeklyPlan(userId);
    }

    /*
     * handleGetTodaysPlanRequest
     * called by DashboardController to show just today's exercises.
     * convenience wrapper — returns only the current day's DayWorkoutPlan.
     * returns null if today is a rest day or if no profile exists.
     */
    public DayWorkoutPlan handleGetTodaysPlanRequest(int userId) {
        return plannerService.getTodaysPlan(userId);
    }

    /*
     * handleGetTodaysSummaryRequest
     * called by DashboardController to populate the "Today's Workout" label.
     * returns a human-readable string like "Push Day · 5 exercises · 50 min".
     * returns "Rest Day" if today is a rest day.
     */
    public String handleGetTodaysSummaryRequest(int userId) {
        return plannerService.getTodaysSummary(userId);
    }

}
