package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationsResonseEntity {

    @SerializedName("activity")
    private ArrayList<NotificationEntity> activityNotifications;
    @SerializedName("to_approve_count")
    private Integer approveCount;

    public ArrayList<NotificationEntity> getActivityNotifications() {
        return activityNotifications;
    }

    public void setActivityNotifications(ArrayList<NotificationEntity> activityNotifications) {
        this.activityNotifications = activityNotifications;
    }

    public Integer getApproveCount() {
        return approveCount;
    }

    public void setApproveCount(Integer approveCount) {
        this.approveCount = approveCount;
    }
}
