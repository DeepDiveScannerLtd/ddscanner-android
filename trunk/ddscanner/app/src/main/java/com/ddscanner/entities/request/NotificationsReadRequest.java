package com.ddscanner.entities.request;

import java.util.ArrayList;

public class NotificationsReadRequest {

    private ArrayList<String> notifications;

    public NotificationsReadRequest(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }
}
