package com.ddscanner.entities.request;

import com.google.gson.annotations.SerializedName;

public class UpdateLocationRequest {

    @SerializedName("app_id")
    private String appId;
    private String lat;
    private String lng;
    @SerializedName("device_type")
    private int deviceType;

    public UpdateLocationRequest(String appId, String lat, String lng, int deviceType) {
        this.appId = appId;
        this.lat = lat;
        this.lng = lng;
        this.deviceType = deviceType;
    }

    public String getAppId() {
        return appId;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public int getDeviceType() {
        return deviceType;
    }
}
