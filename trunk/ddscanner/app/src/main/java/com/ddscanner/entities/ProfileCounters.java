package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileCounters implements Serializable{

    @SerializedName("likes")
    private int likesCount;

    @SerializedName("dislikes")
    private int dislikesCount;

    @SerializedName("checkins")
    private int checkinsCount;

    @SerializedName("additions")
    private int addedCount;

    @SerializedName("changes")
    private int editedCount;

    @SerializedName("reviews")
    private int commentsCount;

    @SerializedName("favorites")
    private int favoritesCount;

    @SerializedName("achievement_points")
    private int points;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public int getCheckinsCount() {
        return checkinsCount;
    }

    public void setCheckinsCount(int checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    public int getAddedCount() {
        return addedCount;
    }

    public void setAddedCount(int addedCount) {
        this.addedCount = addedCount;
    }

    public int getEditedCount() {
        return editedCount;
    }

    public void setEditedCount(int editedCount) {
        this.editedCount = editedCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) {
        this.favoritesCount = favoritesCount;
    }
}
