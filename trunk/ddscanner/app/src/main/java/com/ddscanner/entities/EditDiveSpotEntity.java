package com.ddscanner.entities;

import java.util.List;

/**
 * Created by lashket on 25.1.16.
 */
public class EditDiveSpotEntity {

    private int id;
    private String name;
    private String description;
    private float lat;
    private float lng;
    private float rating;
    private String depth;
    private String visibility;
    private String currents;
    private String level;
    private List<String> images;
    private String diveSpotPathMedium;
    private String diveSpotPathOrigin;
    private String sealifePathMedium;
    private String sealifePathOrigin;
    private String object;
    private String access;
    private String status;
    private boolean isCheckin;
    private boolean isFavorite;
    private String visibilityMin;

    private boolean divespotPathSmallContainsHttp = true;
    private boolean divespotPathMediumContainsHttp = true;
    private boolean divespotPathOriginContainsHttp = true;
    private boolean sealifePathSmallContainsHttp = true;
    private boolean sealifePathMediumContainsHttp = true;
    private boolean sealifePathOriginContainsHttp = true;

    public String getVisibilityMax() {
        return visibilityMax;
    }

    public void setVisibilityMax(String visibilityMax) {
        this.visibilityMax = visibilityMax;
    }

    public String getVisibilityMin() {
        return visibilityMin;
    }

    public void setVisibilityMin(String visibilityMin) {
        this.visibilityMin = visibilityMin;
    }

    private String visibilityMax;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Boolean getValidation() {
        return isValidation;
    }

    public void setValidation(Boolean validation) {
        isValidation = validation;
    }

    public Boolean getCheckin() {
        return isCheckin;
    }

    public void setCheckin(Boolean checkin) {
        isCheckin = checkin;
    }

    private Boolean isValidation;

    public UserOld getCreator() {
        return creator;
    }

    public void setCreator(UserOld creator) {
        this.creator = creator;
    }

    public List<Image> getCommentImages() {
        return commentImages;
    }

    public void setCommentImages(List<Image> commentImages) {
        this.commentImages = commentImages;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    private UserOld creator;
    private List<Image> commentImages;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getCurrents() {
        return currents;
    }

    public void setCurrents(String currents) {
        this.currents = currents;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDiveSpotPathMedium() {
        if (diveSpotPathMedium != null && divespotPathMediumContainsHttp) {
            diveSpotPathMedium = diveSpotPathMedium.replace("http:", "https:");
            divespotPathMediumContainsHttp = false;
        }
        return diveSpotPathMedium;
    }

    public void setDiveSpotPathMedium(String diveSpotPathMedium) {
        this.diveSpotPathMedium = diveSpotPathMedium;
    }

    public String getDiveSpotPathOrigin() {
        if (diveSpotPathOrigin != null && divespotPathOriginContainsHttp) {
            diveSpotPathOrigin = diveSpotPathOrigin.replace("http:", "https:");
            divespotPathOriginContainsHttp = false;
        }
        return diveSpotPathOrigin;
    }

    public void setDiveSpotPathOrigin(String diveSpotPathOrigin) {
        this.diveSpotPathOrigin = diveSpotPathOrigin;
    }

    public String getSealifePathMedium() {
        if (sealifePathMedium != null && sealifePathMediumContainsHttp) {
            sealifePathMedium = sealifePathMedium.replace("http:", "https:");
            sealifePathMediumContainsHttp = false;
        }
        return sealifePathMedium;
    }

    public void setSealifePathMedium(String sealifePathMedium) {
        this.sealifePathMedium = sealifePathMedium;
    }

    public String getSealifePathOrigin() {
        if (sealifePathOrigin != null && sealifePathOriginContainsHttp) {
            sealifePathOrigin = sealifePathOrigin.replace("http:", "https:");
            sealifePathOriginContainsHttp = false;
        }
        return sealifePathOrigin;
    }

    public void setSealifePathOrigin(String sealifePathOrigin) {
        this.sealifePathOrigin = sealifePathOrigin;
    }
}
