package com.ddscanner.entities;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Certificate implements Serializable {

    private long id;
    private String name;
    @SerializedName("what_we_will_learned")
    private String whatWillBeLearned;
    @SerializedName("requirments")
    private String requirements;
    private ArrayList<Certificate> requiredCertificates;
    private int associationType;

    public int getAssociationType() {
        return associationType;
    }

    public void setAssociationType(int associationType) {
        this.associationType = associationType;
    }

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

    public String getWhatWillBeLearned() {
        return whatWillBeLearned;
    }

    public void setWhatWillBeLearned(String whatWillBeLearned) {
        this.whatWillBeLearned = whatWillBeLearned;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public ArrayList<Certificate> getRequiredCertificates() {
        return requiredCertificates;
    }

    public void setRequiredCertificates(ArrayList<Certificate> requiredCertificates) {
        this.requiredCertificates = requiredCertificates;
    }
}
