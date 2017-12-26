package com.ddscanner.entities;


import com.ddscanner.R;
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
    @SerializedName("required_certificates")
    private ArrayList<Certificate> requiredCertificates;
    @SerializedName("association_type")
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

    public Integer getResourceId() {
        switch (associationType) {
            case 1:
                return R.drawable.logo_padi;
            case 3:
                return R.drawable.logo_ssi;

        }
        return null;
    }

}
