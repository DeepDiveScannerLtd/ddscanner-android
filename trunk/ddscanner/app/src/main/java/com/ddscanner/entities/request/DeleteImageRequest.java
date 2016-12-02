package com.ddscanner.entities.request;

import java.util.ArrayList;

public class DeleteImageRequest {

    private ArrayList<String> id = new ArrayList<>();

    public DeleteImageRequest(String image) {
        this.id.add(image);
    }

    public ArrayList<String> getId() {
        return id;
    }
}
