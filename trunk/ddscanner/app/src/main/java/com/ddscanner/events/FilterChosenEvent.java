package com.ddscanner.events;

/**
 * Created by lashket on 1.6.16.
 */
public class FilterChosenEvent {

    private String object;
    private String level;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
