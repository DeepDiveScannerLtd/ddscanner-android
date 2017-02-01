package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Comment {

    private String id;
    private String review;
    private int rating;
    private ArrayList<DiveSpotPhoto> photos;
    private String likes;
    private String dislikes;
    private String date;
    @SerializedName("is_liked")
    private Boolean isLike;
    @SerializedName("is_disliked")
    private Boolean isDislike;
    private ArrayList<SealifeShort> sealifes;

    public ArrayList<SealifeShort> getSealifes() {
        return sealifes;
    }

    public void setSealifes(ArrayList<SealifeShort> sealifes) {
        this.sealifes = sealifes;
    }

    public boolean isLike() {
        if (isLike == null) {
            return false;
        }
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isDislike() {
        if (isDislike == null) {
            return false;
        }
        return isDislike;
    }

    public void setDislike(boolean dislike) {
        isDislike = dislike;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public ArrayList<DiveSpotPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<DiveSpotPhoto> photos) {
        this.photos = photos;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
