package com.ddscanner.entities.request;

import java.util.ArrayList;

public class DeleteImageRequest {

    private ArrayList<String> ids = new ArrayList<>();

    public DeleteImageRequest(String image) {
        this.ids.add(image);
    }

    public ArrayList<String> getId() {
        return ids;
    }
}
