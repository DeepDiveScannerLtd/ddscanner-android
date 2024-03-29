package com.ddscanner.entities;

import com.ddscanner.utils.Helpers;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DiveSpotDetailsEntity extends DiveSpotShort {

    private String description;
    private ArrayList<DiveSpotPhoto> photos;
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
    private List<SealifeShort> sealifes;
    private ArrayList<DiveSpotPhoto> maps;
    @SerializedName("photos_count")
    private int photosCount;
    @SerializedName("maps_count")
    private int mapsPhotosCount;
    @SerializedName("is_edit")
    private Boolean isEdit;
    @SerializedName("cover_id")
    private String coverPhotoId;
    @SerializedName("is_somebody_working_here")
    private boolean isSomebodyWorkingHere;

    public boolean isSomebodyWorkingHere() {
        return isSomebodyWorkingHere;
    }

    public void setSomebodyWorkingHere(boolean somebodyWorkingHere) {
        isSomebodyWorkingHere = somebodyWorkingHere;
    }

    public String getCoverPhotoId() {
        return coverPhotoId;
    }

    public void setCoverPhotoId(String coverPhotoId) {
        this.coverPhotoId = coverPhotoId;
    }

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

    public ArrayList<DiveSpotPhoto> getMaps() {
        return maps;
    }

    public void setMaps(ArrayList<DiveSpotPhoto> maps) {
        this.maps = maps;
    }

    public List<SealifeShort> getSealifes() {
        return sealifes;
    }

    public void setSealifes(List<SealifeShort> sealifes) {
        this.sealifes = sealifes;
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

    public ArrayList<DiveSpotPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<DiveSpotPhoto> photos) {
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
        return verifiedValue != 0;
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
