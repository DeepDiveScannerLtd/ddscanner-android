package com.ddscanner.entities;


import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DailyTour implements Serializable {

    private long id;
    private String name;
    private String photo;
    private String price;
    @SerializedName("number_of_dives")
    private String numberOfDives;
    private String numberOfDivesString;

    public String getNumberOfDives() {
        return numberOfDives;
    }

    public void setNumberOfDives(String numberOfDives) {
        this.numberOfDives = numberOfDives;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
//        return "Diving to Racha Yai";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
//        return "https://pp.userapi.com/c626824/v626824069/3fae/lZ_07Lvm9MA.jpg";
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPrice() {
//            return "2000 THB";
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumberOfDivesString() {
        if (numberOfDives == null) {
            numberOfDivesString = "";
        } else {
            numberOfDivesString = numberOfDives + DDScannerApplication.getInstance().getString(R.string.dives_pattern);
        }
        return numberOfDivesString;
    }
}
