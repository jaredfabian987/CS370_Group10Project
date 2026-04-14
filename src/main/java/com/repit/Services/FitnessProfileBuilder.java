package com.repit.Services;

import com.repit.DAOs.FitnessProfileDAO;
import com.repit.Model.FitnessProfile;


public class FitnessProfileBuilder {
    FitnessProfileDAO fitnessProfileDAO = new FitnessProfileDAO();
    public FitnessProfile saveProfile(int userId, double weight, double height, int daysPerWeek,
                                      double minsAvailablePerWorkout, FitnessProfile.FitnessLevel level, FitnessProfile.FitnessGoal goal){
        FitnessProfile profile = new FitnessProfile(userId, weight, height, daysPerWeek, minsAvailablePerWorkout, level, goal);
        fitnessProfileDAO.saveProfile(profile);
        return fitnessProfileDAO.getProfile(userId);
    }

    public FitnessProfile getProfile(int userId){
        return fitnessProfileDAO.getProfile(userId);
    }

    public void deleteProfile(int profileId){
        fitnessProfileDAO.deleteProfile(profileId);
    }

    public void deleteProfile(FitnessProfile profile){
        fitnessProfileDAO.deleteProfile(profile.getProfileId());
    }
}
