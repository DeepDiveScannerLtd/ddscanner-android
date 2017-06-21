package com.ddscanner.entities;


import com.google.gson.annotations.SerializedName;

public class DiveCenterSearchItem {

    public enum DiveCenterType {

        USER(1), LEGACY(2), NEW(3);

        int integerType;

        DiveCenterType(int integerType) {
            this.integerType = integerType;
        }

        public int getIntegerType() {
            return this.integerType;
        }

    }

    private int id;
    @SerializedName("type")
    private int integerType;
    private String name;
    private String photo;
    private String address;
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIntegerType() {
        return integerType;
    }

    public void setItegerType(int integerType) {
        this.integerType = integerType;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DiveCenterType getDivCenterType() {
        switch (integerType) {
            case 1:
                return DiveCenterType.USER;
            case 2:
                return DiveCenterType.LEGACY;
            case 3:
                return DiveCenterType.NEW;
            default:
                return null;
        }
    }
}
