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
    private DiveSpotShort diveSpotShort;
    private UserOld userOld;
    private CommentOld commentOld;

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

    public DiveSpotShort getDiveSpotShort() {
        return diveSpotShort;
    }

    public void setDiveSpotShort(DiveSpotShort diveSpotShort) {
        this.diveSpotShort = diveSpotShort;
    }

    public UserOld getUserOld() {
        return userOld;
    }

    public void setUserOld(UserOld userOld) {
        this.userOld = userOld;
    }

    public CommentOld getCommentOld() {
        return commentOld;
    }

    public void setCommentOld(CommentOld commentOld) {
        this.commentOld = commentOld;
    }
}
