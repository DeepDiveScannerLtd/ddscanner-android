package com.ddscanner.entities.request;

public class ReportImageRequest {

    private String id;
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
