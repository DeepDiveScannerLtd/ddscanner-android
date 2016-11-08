package com.ddscanner.screens.achievements;

import com.ddscanner.entities.CompleteAchievement;

public class CompletedAchievementItemViewModel {

    private CompleteAchievement completeAchievement;

    public CompletedAchievementItemViewModel(CompleteAchievement completeAchievement) {
        this.completeAchievement = completeAchievement;
    }

    public CompleteAchievement getCompleteAchievement() {
        return completeAchievement;
    }

}
