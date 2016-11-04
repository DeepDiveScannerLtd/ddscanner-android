package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class ProfileCounters {

    @SerializedName("likes")
    private String likesCount;

    @SerializedName("dislikes")
    private String dislikesCount;

    @SerializedName("checkins")
    private String checkinsCount;

    @SerializedName("additions")
    private String addedCount;

    @SerializedName("changes")
    private String editedCount;

    @SerializedName("comments")
    private String commentsCount;

    @SerializedName("favourites")
    private String favoritesCount;

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(String dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public String getCheckinsCount() {
        return checkinsCount;
    }

    public void setCheckinsCount(String checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    public String getAddedCount() {
        return addedCount;
    }

    public void setAddedCount(String addedCount) {
        this.addedCount = addedCount;
    }

    public String getEditedCount() {
        return editedCount;
    }

    public void setEditedCount(String editedCount) {
        this.editedCount = editedCount;
    }

    public String getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(String commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(String favoritesCount) {
        this.favoritesCount = favoritesCount;
    }
}
