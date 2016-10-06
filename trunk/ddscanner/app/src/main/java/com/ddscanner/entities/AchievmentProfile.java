package com.ddscanner.entities;

import java.util.List;

public class AchievmentProfile {


    private String name;
    private List<String> countries;

    public AchievmentProfile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }
}
