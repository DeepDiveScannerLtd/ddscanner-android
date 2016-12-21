package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DiveCenterProfile {

    private int id;
    private String name;
    private String photo;
    private String about;
    private int type;
    @SerializedName("country_name")
    private String contryName;
    private ArrayList<String> addresses;
    private ArrayList<String> emails;
    private ArrayList<String> phones;
    private ArrayList<String> languages;
    @SerializedName("instructors_count")
    private String instructorsCount;
    private ArrayList<String> photos;
    @SerializedName("photos_count")
    private int photosCount;
    @SerializedName("created_spots_count")
    private int createdSpotsCount;
    @SerializedName("edited_spots_count")
    private int editedSpotsCount;
    private String token;
    @SerializedName("where_working_count")
    private int workingCount;

    public int getWorkingCount() {
        return workingCount;
    }

    public void setWorkingCount(int workingCount) {
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

    public String getContryName() {
        return contryName;
    }

    public void setContryName(String contryName) {
        this.contryName = contryName;
    }

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<String> addresses) {
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

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
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
