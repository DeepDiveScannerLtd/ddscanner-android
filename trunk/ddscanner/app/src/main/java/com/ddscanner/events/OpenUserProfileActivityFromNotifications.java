package com.ddscanner.events;

public class OpenUserProfileActivityFromNotifications {

    private String id;
    private int type;

    public OpenUserProfileActivityFromNotifications(String id, int type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }
}
