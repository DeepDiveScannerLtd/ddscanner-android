package com.ddscanner.events;

public class ChangeAccountEvent {

    private String id;

    public ChangeAccountEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
