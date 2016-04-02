package com.ddscanner.entities;

import java.io.Serializable;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class Offer implements Serializable {
//    {
//        "id": "1",
    //     "name": "sadfasdfad
//            "duration": "10 hours",
//            "meetingPoint": "No",
//            "inclusions": "Full board (light breakfast, buffet lunch, snacks, fruits, coffee, tea, soft drinks), guid",
//            "price": "100.00",
//            "hotOffers": false,
//            "hotPrice": null,
//            "symbol": "$"
//    },
    private String id;
    private String name;
    private String duration;
    private String meetingPoint;
    private String inclusions;
    private String price;
    private boolean hotOffers;
    private String hotPrice;
    private String symbol;
    private boolean isExpanded;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMeetingPoint() {
        return meetingPoint;
    }

    public void setMeetingPoint(String meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    public String getInclusions() {
        return inclusions;
    }

    public void setInclusions(String inclusions) {
        this.inclusions = inclusions;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isHotOffers() {
        return hotOffers;
    }

    public void setHotOffers(boolean hotOffers) {
        this.hotOffers = hotOffers;
    }

    public String getHotPrice() {
        return hotPrice;
    }

    public void setHotPrice(String hotPrice) {
        this.hotPrice = hotPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
}
