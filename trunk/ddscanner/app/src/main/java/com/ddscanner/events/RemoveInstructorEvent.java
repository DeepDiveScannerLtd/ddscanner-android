package com.ddscanner.events;

public class RemoveInstructorEvent {

    private String id;
    private int position;
    private String name;

    public RemoveInstructorEvent(String id, int position, String name) {
        this.id = id;
        this.position = position;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
