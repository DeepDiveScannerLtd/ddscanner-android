package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DiveSpotDetailsEntity extends DiveSpotShort {

    private String description;
    private List<Photo> photos;
    private int visibility;
    @SerializedName("visibility_min")
    private String visibilityMin;
    @SerializedName("visibility_max")
    private String visibilityMax;
    private int currents;
    @SerializedName("diving_skill")
    private int diverLevel;
    private float rating;
    @SerializedName("reviews_count")
    private int reviewsCount;
    @SerializedName("is_verified")
    private int verifiedValue;
    private String depth;
    private User author;
    private FlagsEntity flags;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getVisibilityMin() {
        return visibilityMin;
    }

    public void setVisibilityMin(String visibilityMin) {
        this.visibilityMin = visibilityMin;
    }

    public String getVisibilityMax() {
        return visibilityMax;
    }

    public void setVisibilityMax(String visibilityMax) {
        this.visibilityMax = visibilityMax;
    }

    public int getCurrents() {
        return currents;
    }

    public void setCurrents(int currents) {
        this.currents = currents;
    }

    public int getDiverLevel() {
        return diverLevel;
    }

    public void setDiverLevel(int diverLevel) {
        this.diverLevel = diverLevel;
    }

    @Override
    public float getRating() {
        return rating;
    }

    @Override
    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public boolean isVerified() {
        if (verifiedValue == 0) {
            return false;
        }
        return true;
    }

    public void setVerifiedValue(int verifiedValue) {
        this.verifiedValue = verifiedValue;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public FlagsEntity getFlags() {
        return flags;
    }

    public void setFlags(FlagsEntity flags) {
        this.flags = flags;
    }
}
