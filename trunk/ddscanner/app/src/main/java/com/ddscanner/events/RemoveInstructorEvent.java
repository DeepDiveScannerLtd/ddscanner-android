package com.ddscanner.events;

public class RemoveInstructorEvent {

    private String id;
    private int position;

    public RemoveInstructorEvent(String id, int position) {
        this.id = id;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }
}
