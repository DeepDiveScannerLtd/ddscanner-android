package com.ddscanner.entities;


import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DiveCenterSearchItem {

    public enum DiveCenterType {

        USER(1, "user"), LEGACY(2, "legacy"), NEW(3, "new");

        Integer integerType;
        String type;

        DiveCenterType(int integerType, String type) {
            this.type = type;
            this.integerType = integerType;
        }

        public Integer getIntegerType() {
            return this.integerType;
        }

        public String getType() {
            return this.type;
        }

    }

    private int id;
    @SerializedName("type")
    private int integerType;
    private String name;
    private String photo;
    private ArrayList<Address> addresses;
    private String email;
    private CountryEntity country;
    @SerializedName("is_invited")
    private boolean isInvited;

    public boolean isInvited() {
        return isInvited;
    }

    public void setInvited(boolean invited) {
        isInvited = invited;
    }

    public CountryEntity getCountry() {
        return country;
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIntegerType() {
        return integerType;
    }

    public void setIntegerType(int integerType) {
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
        if (addresses == null) {
            return DDScannerApplication.getInstance().getString(R.string.no_address_provided);
        }
        return addresses.get(0).getName();
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
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
