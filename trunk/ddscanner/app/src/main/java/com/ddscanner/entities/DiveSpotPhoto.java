package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class DiveSpotPhoto {

    private String id;
    private String date;
    private User author;
    @SerializedName("is_liked")
    private boolean isLiked;
    @SerializedName("likes_count")
    private String likesCount;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
