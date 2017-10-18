package com.ddscanner.entities;

import java.io.Serializable;

public class BaseIdNamePhotoEntity implements Serializable{

    private String id;
    private String name;
    private String photo;
    private String code;
    private boolean isActive = false;

    public BaseIdNamePhotoEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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
}
