package com.ddscanner.screens.achievements;

import com.ddscanner.entities.PendingAchievement;

public class PendingAchievementItemViewModel {

    private PendingAchievement pendingAchievement;

    public PendingAchievementItemViewModel(PendingAchievement pendingAchievement) {
        this.pendingAchievement = pendingAchievement;
    }

    public PendingAchievement getPendingAchievement() {
        return pendingAchievement;
    }
}
