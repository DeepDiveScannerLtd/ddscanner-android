package com.ddscanner.events;

/**
 * Created by lashket on 6.8.16.
 */
public class SocialLinkOpenEvent {

    private String type;

    public SocialLinkOpenEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
