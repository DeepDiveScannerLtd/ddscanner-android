package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class Instructor {

    private String id;
    private String name;
    private String photo;
    @SerializedName("is_new")
    private Boolean isNew;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Boolean getIsNew() {
        if (isNew == null) {
            return false;
        }
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}
