package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Sealife extends SealifeShort implements Serializable {
    private String length;
    private String weight;
    private String depth;
    @SerializedName("sc_name")
    private String scName;
    private String order;
    private String distribution;
    @SerializedName("class")
    private String scClass;
    private String habitat;

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

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getScClass() {
        return scClass;
    }

    public void setScClass(String scCLass) {
        this.scClass = scCLass;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

}
