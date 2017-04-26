package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class UserLikeEntity {

    private User user;
    @SerializedName("dive_spot")
    private DiveSpotShort diveSpot;
    private Comment review;
    private String date;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DiveSpotShort getDiveSpot() {
        return diveSpot;
    }

    public void setDiveSpot(DiveSpotShort diveSpot) {
        this.diveSpot = diveSpot;
    }

    public Comment getReview() {
        return review;
    }

    public void setReview(Comment review) {
        this.review = review;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
