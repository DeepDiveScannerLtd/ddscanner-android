package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 25.5.16.
 */
public class Notifications implements Serializable{

    private List<Activity> activities;
    private List<Notification> notifications;

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
