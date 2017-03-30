package com.ddscanner.events;

public class OpenDiveSpotDetailsActivityEvent {

    private String id;

    public OpenDiveSpotDetailsActivityEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
