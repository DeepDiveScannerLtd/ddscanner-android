package com.ddscanner.events;

public class ShowDIveSpotDetailsActivityEvent {

    private int position;
    private String id;

    public ShowDIveSpotDetailsActivityEvent(int position, String id) {
        this.position = position;
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public String getId() {
        return id;
    }
}
