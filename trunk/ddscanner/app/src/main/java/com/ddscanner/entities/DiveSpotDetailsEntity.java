package com.ddscanner.entities;

import com.ddscanner.utils.Helpers;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DiveSpotDetailsEntity extends DiveSpotShort {

    private String description;
    private List<String> photos;
    private int visibility;
    @SerializedName("visibility_min")
    private String visibilityMin;
    @SerializedName("visibility_max")
    private String visibilityMax;
    private int currents;
    @SerializedName("diving_skill")
    private int diverLevel;
    @SerializedName("reviews_count")
    private int reviewsCount;
    @SerializedName("is_verified")
    private int verifiedValue;
    private String depth;
    private User author;
    private FlagsEntity flags;
    @SerializedName("checkins_count")
    private int checkinCount;
    @SerializedName("country_name")
    private String countryName;
    private List<DiveSpotSealife> sealifes;
    private List<String> maps;
    @SerializedName("photos_count")
    private int photosCount;
    @SerializedName("maps_count")
    private int mapsPhotosCount;


    public int getMapsPhotosCount() {
        return mapsPhotosCount;
    }

    public void setMapsPhotosCount(int mapsPhotosCount) {
        this.mapsPhotosCount = mapsPhotosCount;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
    }

    public List<String> getMaps() {
        return maps;
    }

    public void setMaps(List<String> maps) {
        this.maps = maps;
    }

    public List<DiveSpotSealife> getSealifes() {
        return sealifes;
    }

    public void setSealifes(List<DiveSpotSealife> sealifes) {
        this.sealifes = sealifes;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getCheckinCount() {
        return checkinCount;
    }

    public void setCheckinCount(int checkinCount) {
        this.checkinCount = checkinCount;
    }

    public int getVerifiedValue() {
        return verifiedValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
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

    public String getCurrents() {
        return Helpers.getCurrentsValue(currents);
    }

    public void setCurrents(int currents) {
        this.currents = currents;
    }

    public String getDiverLevel() {
        return Helpers.getDiverLevel(diverLevel);
    }

    public void setDiverLevel(int diverLevel) {
        this.diverLevel = diverLevel;
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
