package com.ddscanner.entities;

import java.util.ArrayList;

public class Comment {

    private String id;
    private String review;
    private String rating;
    private ArrayList<String> photos;
    private String likes;
    private String dislikes;
    private String date;
    private boolean isLike = true;
    private boolean isDislike = true;

    public boolean isLike() {
        return isLike;
    }

//    public void setLike(boolean like) {
//        isLike = like;
//    }

    public boolean isDislike() {
        return isDislike;
    }

//    public void setDislike(boolean dislike) {
//        isDislike = dislike;
//    }

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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
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
