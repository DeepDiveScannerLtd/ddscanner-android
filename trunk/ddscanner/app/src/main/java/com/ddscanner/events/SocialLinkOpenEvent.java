package com.ddscanner.events;

public class SocialLinkOpenEvent {

    private String type;

    public SocialLinkOpenEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
