package com.ddscanner.events;

public class ImageDeletedEvent {

    private String address;

    public ImageDeletedEvent(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

}
