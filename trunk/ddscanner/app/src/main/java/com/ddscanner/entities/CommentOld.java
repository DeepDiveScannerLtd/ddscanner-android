package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

public class CommentOld implements Serializable{
    private String comment;
    private String rating;
    private String id;
    private List<String> images;
    private String date;
    private String likes;
    private String dislikes;
    private UserOld userOld;
    private boolean isLike;
    private boolean isDislike;
    private boolean isEdit;
    private String diveSpotName;
    private String diveSpotImage;
    private String diveSpotId;

    public String getDiveSpotName() {
        return diveSpotName;
    }

    public void setDiveSpotName(String diveSpotName) {
        this.diveSpotName = diveSpotName;
    }

    public String getDiveSpotImage() {
        return diveSpotImage;
    }

    public void setDiveSpotImage(String diveSpotImage) {
        this.diveSpotImage = diveSpotImage;
    }

    public String getDiveSpotId() {
        return diveSpotId;
    }

    public void setDiveSpotId(String diveSpotId) {
        this.diveSpotId = diveSpotId;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isDislike() {
        return isDislike;
    }

    public void setDislike(boolean dislike) {
        isDislike = dislike;
    }

    /*  protected CommentOld(Parcel in) {
            comment = in.readString();
            rating = in.readString();
            id = in.readString();
            images = in.readList(images, List.class.getClassLoader());
            date = in.readString();
        }
    */
    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserOld getUserOld() {
        return userOld;
    }

    public void setUserOld(UserOld userOld) {
        this.userOld = userOld;
    }
}
