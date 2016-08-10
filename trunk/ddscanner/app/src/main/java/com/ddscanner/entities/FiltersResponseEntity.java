package com.ddscanner.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lashket on 24.1.16.
 */
public class FiltersResponseEntity implements Serializable {
    private Map<String, String> currents;
    private Map<String, String> level;
    private Map<String, String> object;
    private Map<String, String> report;
    private int[] rating;
    private Map<String, String> visibility;
    private Map<String, String> access;

    public FiltersResponseEntity() {
        currents = new HashMap<>();
        level = new HashMap<>();
        object = new HashMap<>();
        visibility = new HashMap<>();
        access = new HashMap<>();
    }

    public Map<String, String> getReport() {
        return report;
    }

    public Map<String, String> getAccess() {
        return access;
    }

    public void setAccess(Map<String, String> access) {
        this.access = access;
    }

    public Map<String, String> getCurrents() {
        return currents;
    }

    public void setCurrents(Map<String, String> currents) {
        this.currents = currents;
    }

    public Map<String, String> getLevel() {
        return level;
    }

    public void setLevel(Map<String, String> level) {
        this.level = level;
    }

    public Map<String, String> getObject() {
        return object;
    }

    public void setObject(Map<String, String> object) {
        this.object = object;
    }

    public int[] getRating() {
        return rating;
    }

    public void setRating(int[] rating) {
        this.rating = rating;
    }

    public Map<String, String> getVisibility() {
        return visibility;
    }

    public void setVisibility(Map<String, String> visibility) {
        this.visibility = visibility;
    }
}
