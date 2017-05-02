package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class Popup {

    private String message;
    private String title;
    @SerializedName("title_name")
    private String titleName;
    private String points;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        if (titleName != null) {
            this.message = this.message.replace("?", titleName);
        }
        if (points != null) {
            this.message = this.message.replace("?", points);
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
