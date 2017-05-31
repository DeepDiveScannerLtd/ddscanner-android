package com.ddscanner.events;

import android.graphics.Bitmap;

public class GetPhotoFromCameraEvent {

    private Bitmap bitmap;

    public GetPhotoFromCameraEvent(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
