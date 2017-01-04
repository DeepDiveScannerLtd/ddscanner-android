package com.ddscanner.entities;

import java.io.Serializable;
import java.util.List;

public class ProfileAchievement implements Serializable {

    private String name;
    private String type;
    private List<String> countries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }
}
