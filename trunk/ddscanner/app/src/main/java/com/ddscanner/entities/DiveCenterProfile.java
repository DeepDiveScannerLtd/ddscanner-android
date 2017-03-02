package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DiveCenterProfile implements Serializable{

    public enum DiveCenterServiceType {
        COMPANY, RESELLER
    }

    private Integer id;
    private String name;
    private String photo;
    private String about;
    private int type;
    @SerializedName("country_name")
    private String countryName;
    @SerializedName("country_code")
    private String countryCode;
    private ArrayList<Address> addresses;
    private ArrayList<String> emails;
    private ArrayList<String> phones;
    private ArrayList<String> languages;
    @SerializedName("instructors_count")
    private String instructorsCount;
    private ArrayList<DiveSpotPhoto> photos;
    @SerializedName("photos_count")
    private int photosCount;
    @SerializedName("created_spots_count")
    private int createdSpotsCount;
    @SerializedName("edited_spots_count")
    private int editedSpotsCount;
    private String token;
    @SerializedName("where_working_count")
    private Integer workingCount;
    @SerializedName("where_working_dive_spots")
    private ArrayList<DiveSpotShort> workingSpots;
    @SerializedName("new_instructors_exist")
    private boolean isNewInstructors;
    @SerializedName("service_type")
    private int serviceType;

    public DiveCenterServiceType getServiceType() {
        switch (serviceType) {
            case 1:
                return DiveCenterServiceType.COMPANY;
            case 2:
                return DiveCenterServiceType.RESELLER;
            default:
                return null;
        }
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isNewInstructors() {
        return isNewInstructors;
    }

    public void setNewInstructors(boolean newInstructors) {
        isNewInstructors = newInstructors;
    }

    public ArrayList<DiveSpotShort> getWorkingSpots() {
        return workingSpots;
    }

    public void setWorkingSpots(ArrayList<DiveSpotShort> workingSpots) {
        this.workingSpots = workingSpots;
    }

    public Integer getWorkingCount() {
        return workingCount;
    }

    public void setWorkingCount(Integer workingCount) {
        this.workingCount = workingCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }

    public ArrayList<String> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<String> phones) {
        this.phones = phones;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<String> languages) {
        this.languages = languages;
    }

    public String getInstructorsCount() {
        return instructorsCount;
    }

    public void setInstructorsCount(String instructorsCount) {
        this.instructorsCount = instructorsCount;
    }

    public ArrayList<DiveSpotPhoto> getPhotos() {
        if (photos != null) {
            for (DiveSpotPhoto diveSpotPhoto : photos) {
                PhotoAuthor photoAuthor = new PhotoAuthor(String.valueOf(id), name, photo, type);
                photos.get(photos.indexOf(diveSpotPhoto)).setAuthor(photoAuthor);
            }
            return photos;
        }
        return photos;
    }

    public void setPhotos(ArrayList<DiveSpotPhoto> photos) {
        this.photos = photos;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
    }

    public int getCreatedSpotsCount() {
        return createdSpotsCount;
    }

    public void setCreatedSpotsCount(int createdSpotsCount) {
        this.createdSpotsCount = createdSpotsCount;
    }

    public int getEditedSpotsCount() {
        return editedSpotsCount;
    }

    public void setEditedSpotsCount(int editedSpotsCount) {
        this.editedSpotsCount = editedSpotsCount;
    }
}
