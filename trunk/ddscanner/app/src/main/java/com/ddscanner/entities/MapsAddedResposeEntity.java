package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapsAddedResposeEntity {

    @SerializedName("map_ids")
    private List<Integer> ids;

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
