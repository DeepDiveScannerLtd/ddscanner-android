package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class DiverNotification extends DiveCenterActivityNotification {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
