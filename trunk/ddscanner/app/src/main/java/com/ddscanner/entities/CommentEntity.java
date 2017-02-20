package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CommentEntity {

    public enum ReviewType {
        USER, DIVESPOT
    }

    @SerializedName("review")
    private Comment comment;
    private User author;
    @SerializedName("dive_spot")
    private DiveSpotShort diveSpot;
    private ArrayList<SealifeShort> sealifes;
    private boolean isRequestSent = false;

    public DiveSpotShort getDiveSpot() {
        return diveSpot;
    }

    public void setDiveSpot(DiveSpotShort diveSpot) {
        this.diveSpot = diveSpot;
    }

    public ArrayList<SealifeShort> getSealifes() {
        return sealifes;
    }

    public void setSealifes(ArrayList<SealifeShort> sealifes) {
        this.sealifes = sealifes;
    }

    public boolean isRequestSent() {
        return isRequestSent;
    }

    public void setRequestSent(boolean requestSent) {
        isRequestSent = requestSent;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public ReviewType getReviewType() {
        if (author == null) {
            return ReviewType.USER;
        }
        return ReviewType.DIVESPOT;
    }

}
