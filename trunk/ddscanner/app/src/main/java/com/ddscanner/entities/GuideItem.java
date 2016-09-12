package com.ddscanner.entities;

import java.io.Serializable;

/**
 * Created by lashket on 5.8.16.
 */
public class GuideItem implements Serializable {

    private String title;
    private String description;

    public GuideItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
