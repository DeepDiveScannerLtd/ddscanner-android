package com.ddscanner.entities;


import com.ddscanner.utils.Helpers;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FunDive implements Serializable {

    private long id;
    private String name;
    private String photo;
    private Integer level;
    @SerializedName("price_from")
    private String priceFrom;
    private String diverLevelString;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(String priceFrom) {
        this.priceFrom = priceFrom;
    }

    public String getDiverLevelString() {
        if (level != null) {
            return Helpers.getDiverLevel(level);
        }
        return "";
    }

}
