package com.ddscanner.entities.request;

import com.google.gson.annotations.SerializedName;

public class ReportImageRequest {

    private String id;
    @SerializedName("report_type")
    private int type;
    private String description;

    public ReportImageRequest(String id, int type, String description) {
        this.id = id;
        this.type = type;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
