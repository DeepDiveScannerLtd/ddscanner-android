package com.ddscanner.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Offer implements Serializable {

    private String id;
    private String diveCenterName;
    private String offerName;
    private String price;
    private ArrayList<String> spots;
    private String image;

    public Offer(String id, String diveCenterName, String offerName, String price, ArrayList<String> spots, String image) {
        this.id = id;
        this.diveCenterName = diveCenterName;
        this.offerName = offerName;
        this.price = price;
        this.spots = spots;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiveCenterName() {
        return diveCenterName;
    }

    public void setDiveCenterName(String diveCenterName) {
        this.diveCenterName = diveCenterName;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<String> getSpots() {
        return spots;
    }

    public void setSpots(ArrayList<String> spots) {
        this.spots = spots;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAllSpotsAsString() {
        String outputString = "";
        if (this.spots != null) {
            for (String name: spots) {
                outputString += name;
                if (spots.indexOf(name) != spots.size() - 1) {
                    outputString += ", ";
                }
            }
        }
        return outputString;
    }

}
