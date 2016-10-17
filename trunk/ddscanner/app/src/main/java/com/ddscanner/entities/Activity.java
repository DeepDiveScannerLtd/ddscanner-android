package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lashket on 25.5.16.
 */
public class Activity implements Serializable {

    public enum ActivityType {

        @SerializedName("checkin")
        CHECKIN,

        @SerializedName("store")
        STORE,

        @SerializedName("update")
        UPDATE,

        @SerializedName("achieve")
        ACHIEVE

    }

    private ActivityType type;
    private String date;
    private String message;
    private DiveSpot diveSpot;
    private User user;

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DiveSpot getDiveSpot() {
        return diveSpot;
    }

    public void setDiveSpot(DiveSpot diveSpot) {
        this.diveSpot = diveSpot;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
