package com.repit.Services;

import com.repit.DAOs.AvailabilityDAO;
import com.repit.DAOs.ExercisesDAO;
import com.repit.DAOs.FitnessProfileDAO;
import com.repit.DAOs.WorkoutLogsDAO;
import com.repit.Model.Availability;
import com.repit.Model.DayWorkoutPlan;
import com.repit.Model.Exercise;
import com.repit.Model.FitnessProfile;
import com.repit.Model.PlannedExercise;
import com.repit.Model.WorkoutPlan;
import com.repit.Model.enums.ExerciseType;
import com.repit.Model.enums.WorkoutType;
import com.repit.util.ExercisePriorityQueue;
import com.repit.util.SplitSelector;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PlannerService
 * Builds the user's weekly workout plan and tracks day-level completion status.
 *
 * This service is the entry point for the planner screen on the dashboard.
 * It answers two questions:
 *   1. "What exercises am I doing each day this week?"
 *   2. "Which of those days have I already completed?"
 *
 * Responsibilities:
 * - Reading the user's fitness profile to know their goal, days/week, and time budget
 * - Delegating split selection to SplitSelector (which days get which workout type)
 * - Delegating exercise ranking to ExercisePriorityQueue (which exercises go on each day)
 * - Checking WorkoutLogsDAO to mark which days are already done this week
 *
 * Rest day rule:
 * Rest days are simply the days the user is NOT available to train — they are not
 * chosen directly, they are whatever days remain after training days are assigned.
 * For 1-5 training days, the gaps between sessions are rest days.
 * For 6-7 training days there are no gaps — the split just continues across every
 * day of the week with no forced rest days from availability. In the 7-day case
 * SplitSelector caps the unique workout types at 6 and repeats as needed.
 *
 * Note: Training days currently default based on daysPerWeek (Monday-anchored).
 * Once AvailabilityDAO is built, it will supply the exact days the user marked as
 * available, and rest days will be derived from whatever days are NOT in that set.
 */
public class PlannerService {

    private final FitnessProfileDAO fitnessProfileDAO;
    private final ExercisesDAO exercisesDAO;
    private final WorkoutLogsDAO workoutLogsDAO;
    private final AvailabilityDAO availabilityDAO;

    // minimum available minutes if profile has a very small value —
    // we need at least 30 min for 3 exercises (2 compound + 1 isolation)
    private static final int MIN_WORKOUT_MINUTES = 30;

    // each exercise slot = 10 minutes: 2 warmup sets + 2 working sets + rest between sets
    private static final int MINUTES_PER_EXERCISE = 10;

    public PlannerService(FitnessProfileDAO fitnessProfileDAO,
                          ExercisesDAO exercisesDAO,
                          WorkoutLogsDAO workoutLogsDAO,
                          AvailabilityDAO availabilityDAO) {
        this.fitnessProfileDAO = fitnessProfileDAO;
        this.exercisesDAO = exercisesDAO;
        this.workoutLogsDAO = workoutLogsDAO;
        this.availabilityDAO = availabilityDAO;
    }

    // PUBLIC API

    /**
     * Builds and returns the user's full weekly plan for the current Mon-Sun week.
     *
     * The returned map contains an entry for every day of the week (Monday through Sunday).
     * Training days have a DayWorkoutPlan populated with exercises.
     * Rest days have a DayWorkoutPlan where isRestDay = true and the exercise list is empty.
     * Days the user has already completed have isCompleted = true.
     *
     * Returns null if the user has not completed setup (no fitness profile found).
     *
     * @param userId the logged-in user's ID
     * @return ordered map of DayOfWeek -> DayWorkoutPlan, or null if no profile exists
     */
    public Map<DayOfWeek, DayWorkoutPlan> getWeeklyPlan(int userId) {

        // STEP 1: Load the user's fitness profile
        // The profile gives us three things we need for generation:
        //   - goal: drives which split SplitSelector picks (muscle building vs weight loss)
        //   - daysPerWeek: how many training days to schedule (1-7)
        //   - minsAvailablePerWorkout: time budget per session → how many exercises fit
        FitnessProfile profile = fitnessProfileDAO.getProfile(userId);
        if (profile == null) {
            // user hasn't completed setup — controller should redirect to setup screen
            return null;
        }

        // STEP 2: Determine which days of the week the user trains
        // First preference: the user's saved Availability (specific days they picked
        // on the setup/planner screen). Fallback: default pattern based on daysPerWeek
        // for legacy profiles that were saved before AvailabilityDAO was wired in.
        Availability availability = availabilityDAO.getAvailability(userId);
        Set<DayOfWeek> trainingDays;
        if (availability != null && !availability.getAvailableDays().isEmpty()) {
            trainingDays = new LinkedHashSet<>(availability.getAvailableDays());
        } else {
            trainingDays = getDefaultTrainingDays(profile.getDaysPerWeek());
        }

        // STEP 3: Map the user's goal to the string SplitSelector expects ───
        // FitnessProfile uses the FitnessGoal enum (BUILD, MUSCLE, MAINTAIN).
        // SplitSelector uses plain strings ("MUSCLE_BUILDING", "WEIGHT_LOSS").
        String goalString = mapGoalToSplitString(profile.getGoal());

        //  STEP 4: Pick the workout split
        // SplitSelector looks at how many days they train and their goal,
        // then returns an ordered list of WorkoutTypes — one per training day.
        // Example: 3 days, MUSCLE_BUILDING → [UPPER, LOWER, FULL_BODY]
        // Example: 5 days, MUSCLE_BUILDING → [PUSH, PULL, LEGS, UPPER, LOWER]
        //
        // The list order matches the recommended training sequence for recovery:
        // push muscles recover while pull day runs, then legs gives the upper body a break.
        List<WorkoutType> split = SplitSelector.selectSplit(trainingDays, goalString);

        //  STEP 5: Resolve the time budget (per-day if availability is set,
        //          otherwise fall back to profile.minsAvailablePerWorkout)
        // minsAvailablePerWorkout might not be a clean multiple of 10.
        // We floor it to the nearest 10-minute block because each exercise takes exactly 10 min.
        // We also enforce a minimum of 30 minutes (at least 3 exercises) so the queue
        // never returns fewer than the 2-compound minimum.
        int fallbackRawMinutes = (int) profile.getMinsAvailablePerWorkout();
        int fallbackMinutes = snapToTenMinBlock(fallbackRawMinutes);

        // STEP 6: Load all exercises available to this user
        // This includes both the global exercise library and any custom exercises
        // the user has created. ExercisePriorityQueue will filter and rank them
        // based on which workout type each day is assigned.
        ArrayList<Exercise> allExercises = exercisesDAO.getExercises(userId);
        if (allExercises == null || allExercises.isEmpty()) {
            // no exercises seeded yet — return empty plan rather than crashing
            return buildEmptyWeek();
        }

        // STEP 7: Check which days the user has already completed this week
        // WorkoutLogsDAO returns the set of dates (Mon-Sun) where the user logged
        // at least one completed set. We use this to set isCompleted on each DayWorkoutPlan.
        // A day is "done" when its calendar date appears in this set.
        Set<LocalDate> completedDates = workoutLogsDAO.getLoggedDatesThisWeek(userId);

        // STEP 8: Build a DayWorkoutPlan for every day of the week
        // We walk Monday through Sunday in order.
        // Training days get a full exercise list built by ExercisePriorityQueue.
        // Rest days get an empty DayWorkoutPlan with isRestDay = true.
        // Each day gets isCompleted set by checking the logged dates from Step 7.
        Map<DayOfWeek, DayWorkoutPlan> weeklyPlan = new LinkedHashMap<>();

        // pair each training day (calendar order) with its WorkoutType (split order)
        List<DayOfWeek> orderedTrainingDays = new ArrayList<>(trainingDays);
        ExercisePriorityQueue priorityQueue = new ExercisePriorityQueue();

        for (DayOfWeek day : DayOfWeek.values()) {

            DayWorkoutPlan dayPlan = new DayWorkoutPlan(day);

            if (trainingDays.contains(day)) {

                // find what position this day is in the training schedule
                // so we can look up the matching WorkoutType from the split
                int splitIndex = orderedTrainingDays.indexOf(day);
                WorkoutType workoutType = (splitIndex < split.size())
                        ? split.get(splitIndex)
                        : WorkoutType.FULL_BODY; // fallback if split is shorter

                // resolve this day's specific time budget — per-day if availability
                // has it, otherwise the profile-wide fallback
                int availableMinutes;
                if (availability != null && availability.isTrainingDay(day)) {
                    availableMinutes = snapToTenMinBlock(availability.getMinutesForDay(day));
                } else {
                    availableMinutes = fallbackMinutes;
                }
                dayPlan.setEstimatedDurationMinutes(availableMinutes);

                // STEP 8a: Select exercises for this day
                // CARDIO days are handled differently from strength days —
                // the priority queue is skipped and we just pick one cardio
                // exercise that fills the user's full available time.
                // Strength days (PUSH, PULL, LEGS, UPPER, LOWER, FULL_BODY) use
                // the priority queue to rank and select multiple exercises.
                if (workoutType == WorkoutType.CARDIO) {

                    // pick one cardio exercise — just one activity for the session
                    Exercise cardioExercise = selectCardioExercise(allExercises);

                    if (cardioExercise != null) {
                        PlannedExercise planned = new PlannedExercise(cardioExercise);
                        dayPlan.addExercise(planned);
                    }

                    // workout name is just the type — the UI appends the duration
                    // separately from dayPlan.getEstimatedDurationMinutes()
                    dayPlan.setWorkoutName("Cardio");

                } else {

                    // strength day — rank all exercises by muscle group match,
                    // then fill compound slots first and isolation slots after
                    // number of slots = availableMinutes / 10
                    // Example: 50 min → 5 exercises (3 compounds + 2 isolations on PUSH)
                    List<Exercise> rankedExercises = priorityQueue.buildQueue(
                            allExercises, workoutType, availableMinutes);

                    // STEP 8b: Wrap each Exercise in a PlannedExercise
                    // PlannedExercise adds sets, reps, and suggested weight context.
                    // Defaults: 2 warmup sets + 2 working sets (standard protocol).
                    // suggestedWeight is 0 here — the workout controller fills it in
                    // using ProgressService (last logged weight for that exercise).
                    for (Exercise exercise : rankedExercises) {
                        PlannedExercise planned = new PlannedExercise(exercise);
                        dayPlan.addExercise(planned);
                    }

                    dayPlan.setWorkoutName(workoutType.name());
                }

                dayPlan.setRestDay(false);

            } else {
                // rest day — this day is not in the user's availability.
                // rest days are never chosen directly by the user; they are simply
                // whichever days of the week are NOT marked as training days.
                // for 6 or 7 training days there are no rest days from availability —
                // the split continues every day with no gaps.
                dayPlan.setRestDay(true);
                dayPlan.setWorkoutName("Rest Day");
            }

            // ── STEP 8c: Mark the day as completed if the user already logged it ─
            // Convert this day's DayOfWeek to the actual calendar date for this week.
            // If that date is in the completedDates set, the user already did this workout.
            LocalDate dayDate = LocalDate.now().with(day);
            dayPlan.setCompleted(completedDates.contains(dayDate));

            weeklyPlan.put(day, dayPlan);
        }

        return weeklyPlan;
    }

    /**
     * Returns the workout plan for today only.
     * Convenience wrapper around getWeeklyPlan() — useful for the dashboard
     * to show just today's exercises without rendering the full week.
     *
     * Returns null if no profile exists or if today is a rest day.
     *
     * @param userId the logged-in user's ID
     * @return today's DayWorkoutPlan, or null if rest day or no profile
     */
    public DayWorkoutPlan getTodaysPlan(int userId) {
        Map<DayOfWeek, DayWorkoutPlan> weeklyPlan = getWeeklyPlan(userId);
        if (weeklyPlan == null) return null;

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        DayWorkoutPlan todaysPlan = weeklyPlan.get(today);

        // return null for rest days so callers don't try to render an empty exercise list
        if (todaysPlan == null || todaysPlan.isRestDay()) return null;
        return todaysPlan;
    }

    /**
     * Returns a human-readable summary of today's workout for the dashboard header.
     * Format: "Push Day · 3 exercises · 30 min"
     * Returns "Rest Day" if today is a rest day.
     * Returns "Setup not complete" if the user has no profile.
     *
     * @param userId the logged-in user's ID
     * @return formatted summary string for display on the dashboard
     */
    public String getTodaysSummary(int userId) {
        DayWorkoutPlan todaysPlan = getTodaysPlan(userId);
        if (todaysPlan == null) return "Rest Day";

        int exerciseCount = todaysPlan.getExercises().size();
        // prefer the user's scheduled budget (set by getWeeklyPlan from Availability);
        // fall back to exercises*10 if no schedule was saved
        int totalMinutes = todaysPlan.getEstimatedDurationMinutes();
        if (totalMinutes <= 0) totalMinutes = exerciseCount * MINUTES_PER_EXERCISE;
        return todaysPlan.getWorkoutName() + " · "
                + exerciseCount + " exercises · "
                + totalMinutes + " min";
    }


    // PRIVATE HELPERS

    /**
     * Floors a raw minute value to the nearest 10-minute block, with a 30-minute
     * minimum. Each exercise slot is exactly 10 minutes (2 warmup + 2 working sets),
     * so we can't have partial slots — and we always want room for at least
     * 2 compounds + 1 isolation.
     */
    private int snapToTenMinBlock(int rawMinutes) {
        return Math.max(MIN_WORKOUT_MINUTES,
                (rawMinutes / MINUTES_PER_EXERCISE) * MINUTES_PER_EXERCISE);
    }



    /**
     * Returns the default set of training days for a given daysPerWeek count.
     *
     * Rest day rule:
     * Rest days are NOT picked by the user — they are whichever days are NOT
     * in the returned training day set. For 1-5 days the gaps between sessions
     * become the rest days. For 6-7 days the split runs continuously with no
     * gaps, so there are no rest days derived from availability.
     *
     * Default patterns (designed to space sessions for recovery where possible):
     *   1 day  → Wednesday only (midweek, one full-body session)
     *   2 days → Monday, Thursday (3 days apart, equal recovery on both sides)
     *   3 days → Monday, Wednesday, Friday (rest day between every session)
     *   4 days → Monday, Tuesday, Thursday, Friday (one back-to-back pair per half-week)
     *   5 days → Monday through Friday (weekends become the rest days)
     *   6 days → Monday through Saturday (split continues daily, Sunday is the only rest day)
     *   7 days → all 7 days (split continues with no rest days from availability;
     *             SplitSelector caps unique workout types at 6 and repeats as needed)
     *
     * Returns a LinkedHashSet to preserve calendar order (Monday first).
     *
     * TODO: This will be replaced by AvailabilityDAO once it is built.
     * AvailabilityDAO will store the exact days the user marked as available.
     * Rest days will then be derived automatically as whatever days are NOT in
     * that set — the same rule applies regardless of how many days they train.
     *
     * @param daysPerWeek how many days per week the user wants to train
     * @return ordered set of DayOfWeek values representing training days
     */
    private Set<DayOfWeek> getDefaultTrainingDays(int daysPerWeek) {
        Set<DayOfWeek> days = new LinkedHashSet<>();
        switch (Math.min(daysPerWeek, 7)) {
            case 1:
                days.add(DayOfWeek.WEDNESDAY);
                break;
            case 2:
                days.addAll(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.THURSDAY));
                break;
            case 3:
                days.addAll(Arrays.asList(
                        DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
                break;
            case 4:
                days.addAll(Arrays.asList(
                        DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
                break;
            case 5:
                days.addAll(Arrays.asList(
                        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
                break;
            case 6:
                days.addAll(Arrays.asList(
                        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY));
                break;
            default: // 7
                days.addAll(Arrays.asList(DayOfWeek.values()));
                break;
        }
        return days;
    }

    /**
     * Converts FitnessProfile.FitnessGoal into the string SplitSelector expects.
     *
     * SplitSelector was written to accept "MUSCLE_BUILDING" or "WEIGHT_LOSS".
     * FitnessProfile uses the FitnessGoal enum (BUILD, MUSCLE, MAINTAIN).
     * Mapping:
     *   BUILD    → "MUSCLE_BUILDING" (user wants to add size and strength)
     *   MUSCLE   → "MUSCLE_BUILDING" (same focus, different label in the UI)
     *   MAINTAIN → "WEIGHT_LOSS"     (maintenance goals favor mixed cardio splits)
     *
     * @param goal the user's FitnessGoal from their profile
     * @return the matching string for SplitSelector
     */
    private String mapGoalToSplitString(FitnessProfile.FitnessGoal goal) {
        if (goal == null) return "MUSCLE_BUILDING";
        switch (goal) {
            case BUILD:
            case MUSCLE:
                return "MUSCLE_BUILDING";
            case MAINTAIN:
            default:
                return "WEIGHT_LOSS";
        }
    }

    /**
     * Picks one cardio exercise from the full exercise list for a CARDIO day.
     *
     * Cardio days use only ONE exercise that fills the user's entire available time
     * (e.g. 30 min of running, 45 min of cycling). The priority queue is not used
     * because there is no muscle-group scoring for cardio — any cardio exercise works.
     *
     * Selection: first exercise in the list with ExerciseType.CARDIO.
     * Returns null if no cardio exercises exist in the database yet.
     *
     * @param allExercises the full exercise list loaded from ExercisesDAO
     * @return one cardio Exercise, or null if none are seeded
     */
    private Exercise selectCardioExercise(List<Exercise> allExercises) {
        for (Exercise exercise : allExercises) {
            // find the first exercise tagged as CARDIO type in the database
            if (exercise.getExerciseType() == ExerciseType.CARDIO) {
                return exercise;
            }
        }
        // no cardio exercises seeded yet — caller handles the null gracefully
        return null;
    }

    /**
     * Builds a full week of empty rest-day plans.
     * Used as a fallback when no exercises are seeded in the database yet.
     *
     * @return map of DayOfWeek -> empty DayWorkoutPlan with isRestDay = true
     */
    private Map<DayOfWeek, DayWorkoutPlan> buildEmptyWeek() {
        Map<DayOfWeek, DayWorkoutPlan> week = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            DayWorkoutPlan plan = new DayWorkoutPlan(day);
            plan.setRestDay(true);
            plan.setWorkoutName("Rest Day");
            week.put(day, plan);
        }
        return week;
    }
}
