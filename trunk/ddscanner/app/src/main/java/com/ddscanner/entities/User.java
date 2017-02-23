package com.ddscanner.entities;

import com.ddscanner.utils.Helpers;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String name;
    private String photo;
    private ProfileCounters counters;
    private List<ProfileAchievement> achievements;
    private List<DiveSpotPhoto> photos;
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

    public List<DiveSpotPhoto> getPhotos() {
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

    public void setPhotos(List<DiveSpotPhoto> photos) {
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
