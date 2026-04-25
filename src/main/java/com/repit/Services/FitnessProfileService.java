package com.repit.Services;

import com.repit.DAOs.FitnessProfileDAO;
import com.repit.Model.FitnessProfile;

/**
 * FitnessProfileService
 * Handles all fitness profile business logic.
 *
 * Responsibilities:
 * - Saving a new profile after the user completes setup
 * - Retrieving the profile to check setup completion and read user preferences
 * - Updating the profile when the user changes their fitness settings
 *
 * Replaces FitnessProfileBuilder. Controllers never call FitnessProfileDAO directly.
 */
public class FitnessProfileService {

    private final FitnessProfileDAO fitnessProfileDAO;

    public FitnessProfileService(FitnessProfileDAO fitnessProfileDAO) {
        this.fitnessProfileDAO = fitnessProfileDAO;
    }

    /**
     * Saves a new fitness profile after the user completes the setup flow.
     *
     * @param profile the completed profile to persist
     * @return true if the profile was saved successfully
     */
    public boolean saveProfile(FitnessProfile profile) {
        return fitnessProfileDAO.saveProfile(profile);
    }

    /**
     * Returns the fitness profile for a user.
     * Returns null if the user has not completed setup yet.
     *
     * @param userId the logged-in user's ID
     * @return the user's FitnessProfile, or null
     */
    public FitnessProfile getProfile(int userId) {
        return fitnessProfileDAO.getProfile(userId);
    }

    /**
     * Updates an existing fitness profile.
     * Called when the user edits their goals, available days, or other preferences.
     *
     * @param profile the updated profile to persist
     * @return true if the update was successful
     */
    public boolean updateProfile(FitnessProfile profile) {
        return fitnessProfileDAO.updateProfile(profile);
    }
}
