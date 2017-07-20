package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

public class Notifications implements Serializable{

    private List<Activity> activities;
    private List<NotificationOld> notificationOlds;

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<NotificationOld> getNotificationOlds() {
        return notificationOlds;
    }

    public void setNotificationOlds(List<NotificationOld> notificationOlds) {
        this.notificationOlds = notificationOlds;
    }
}
