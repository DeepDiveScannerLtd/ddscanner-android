package com.ddscanner.entities;

import com.ddscanner.utils.Helpers;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String name;
    private String photo;
    private ProfileCounters counters;
    private List<ProfileAchievement> achievements;
    private ArrayList<DiveSpotPhoto> photos;
    private int type;
    private String token;
    @SerializedName("diving_skill")
    private Integer diverLevel;
    @SerializedName("about")
    private String about;
    private String id;
    @SerializedName("photos_count")
    private int photosCount;
    @SerializedName("dive_center")
    private DiveCenterProfile diveCenter;
    @SerializedName("is_creator")
    private boolean isCreator;
    @SerializedName("provider_id")
    private String facebookLink;
    @SerializedName("provider_type")
    private Integer providerType;
    private String link;
    @SerializedName("dc_type")
    private int diveCenterType;

    public int getDiveCenterType() {
        return diveCenterType;
    }

    public void setDiveCenterType(int diveCenterType) {
        this.diveCenterType = diveCenterType;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public Integer getProviderType() {
        return providerType;
    }

    public void setProviderType(Integer providerType) {
        this.providerType = providerType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public DiveCenterProfile getDiveCenter() {
        return diveCenter;
    }

    public void setDiveCenter(DiveCenterProfile diveCenter) {
        this.diveCenter = diveCenter;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDiverLevel() {
        return diverLevel;
    }

    public void setDiverLevel(Integer diverLevel) {
        this.diverLevel = diverLevel;
    }

    public String getDiverLevelString() {
        switch (type) {
            case 1:
                return Helpers.getDiverLevel(diverLevel);
            case 2:
                return "Instructor";
            default:
                return "";
        }

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<DiveSpotPhoto> getPhotos() {
        if (photos != null) {
            for (DiveSpotPhoto diveSpotPhoto : photos) {
                PhotoAuthor photoAuthor = new PhotoAuthor(id, name, photo, type);
                photos.get(photos.indexOf(diveSpotPhoto)).setAuthor(photoAuthor);
            }
            return photos;
        }
        return photos;
    }

    public String getUserTypeString() {
        switch (type) {
            case 0:
                return "Dive center";
            case 1:
                return "Diver";
            case 2:
                return "Instructor";
            default:
                return "";
        }
    }

    public void setPhotos(ArrayList<DiveSpotPhoto> photos) {
        this.photos = photos;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        if (photo == null) {
            return "";
        }
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ProfileCounters getCounters() {
        return counters;
    }

    public void setCounters(ProfileCounters counters) {
        this.counters = counters;
    }

    public List<ProfileAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<ProfileAchievement> achievements) {
        this.achievements = achievements;
    }
}
