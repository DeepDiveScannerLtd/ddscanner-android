package com.ddscanner.events;

public class LocationChosedEvent {

    private String id;

    public LocationChosedEvent(String id) {
        this.id = id;
    }

    public String getLatLngBounds() {
        return this.id;
    }

}
