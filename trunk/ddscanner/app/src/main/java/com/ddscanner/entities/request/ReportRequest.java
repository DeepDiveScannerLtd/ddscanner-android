package com.ddscanner.entities.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lashket on 10.8.16.
 */
public class ReportRequest {

    @SerializedName("report_type")
    private String type;
    private String description;
    private String id;

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public ReportRequest(String type, String description, String id) {
        this.type = type;
        this.description = description;
        this.id = id;
    }
}
