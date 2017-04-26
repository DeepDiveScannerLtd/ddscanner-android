package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LikeEntity {

    public enum LikeType {
        REVIEW, PHOTO
    }

    private User user;
    @SerializedName("dive_spot")
    private DiveSpotShort diveSpot;
    private DiveSpotPhoto photo;
    private String date;
    private int type;
    private Comment review;

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

    public ArrayList<DiveSpotPhoto> getPhoto() {
        ArrayList<DiveSpotPhoto> photos = new ArrayList<>();
        PhotoAuthor author = new PhotoAuthor(user.getId(), user.getName(), user.getPhoto(), user.getType());
        photo.setAuthor(author);
        photos.add(photo);
        return photos;
    }

    public void setPhoto(DiveSpotPhoto photo) {
        this.photo = photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LikeType getType() {
        switch (type) {
            case 1:
                return LikeType.REVIEW;
            case 2:
                return LikeType.PHOTO;
            default:
                return null;
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public Comment getReview() {
        return review;
    }

    public void setReview(Comment review) {
        this.review = review;
    }
}
