package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class ProfileCounters {

    @SerializedName("likes")
    private Integer likesCount;

    @SerializedName("dislikes")
    private Integer dislikesCount;

    @SerializedName("checkins")
    private Integer checkinsCount;

    @SerializedName("additions")
    private Integer addedCount;

    @SerializedName("changes")
    private Integer editedCount;

    @SerializedName("comments")
    private Integer commentsCount;

    @SerializedName("favourites")
    private Integer favoritesCount;

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(Integer dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public Integer getCheckinsCount() {
        return checkinsCount;
    }

    public void setCheckinsCount(Integer checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    public Integer getAddedCount() {
        return addedCount;
    }

    public void setAddedCount(Integer addedCount) {
        this.addedCount = addedCount;
    }

    public Integer getEditedCount() {
        return editedCount;
    }

    public void setEditedCount(Integer editedCount) {
        this.editedCount = editedCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(Integer favoritesCount) {
        this.favoritesCount = favoritesCount;
    }
}
