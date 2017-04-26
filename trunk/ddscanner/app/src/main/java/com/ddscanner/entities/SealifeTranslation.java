package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class SealifeTranslation {

    @SerializedName("content_lang")
    private String langCode;
    private String name;
    private String length;
    private String weight;
    private String depth;
    @SerializedName("sc_name")
    private String scName;
    private String order;
    private String distribution;
    @SerializedName("class")
    private String sealifeClass;
    private String habitat;

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getScName() {
        return scName;
    }

    public void setScName(String scName) {
        this.scName = scName;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getSealifeClass() {
        return sealifeClass;
    }

    public void setSealifeClass(String sealifeClass) {
        this.sealifeClass = sealifeClass;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }
}
