package com.ddscanner.events;

public class ImageDeletedEvent {

    private int imageIndex;

    public ImageDeletedEvent(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public int getImageIndex() {
        return this.imageIndex;
    }

}
