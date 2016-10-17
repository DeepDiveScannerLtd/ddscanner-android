package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lashket on 25.5.16.
 */
public class Notification implements Serializable {

    public static enum Type{
        @SerializedName("like")
        LIKE,

        @SerializedName("dislike")
        DISLIKE,

        @SerializedName("accept")
        ACCEPT,

        @SerializedName("achieve")
        ACHIEVE
    };
    @SerializedName("type")
    private Type type;
    private String date;
    private String message;
    private DiveSpot diveSpot;
    private User user;
    private Comment comment;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
