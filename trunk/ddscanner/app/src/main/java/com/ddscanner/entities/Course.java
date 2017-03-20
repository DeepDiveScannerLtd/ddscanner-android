package com.ddscanner.entities;

import java.util.ArrayList;

public class Course {

    private String id;
    private String diveCenterName;
    private String courceName;
    private String courceLength;
    private String image;
    private String diveCenterLogo;
    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiveCenterName() {
        return diveCenterName;
    }

    public void setDiveCenterName(String diveCenterName) {
        this.diveCenterName = diveCenterName;
    }

    public String getCourceName() {
        return courceName;
    }

    public void setCourceName(String courceName) {
        this.courceName = courceName;
    }

    public String getCourceLength() {
        return courceLength;
    }

    public void setCourceLength(String courceLength) {
        this.courceLength = courceLength;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDiveCenterLogo() {
        return diveCenterLogo;
    }

    public void setDiveCenterLogo(String diveCenterLogo) {
        this.diveCenterLogo = diveCenterLogo;
    }
}
