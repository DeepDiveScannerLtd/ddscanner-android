package com.ddscanner.events;

import android.graphics.Bitmap;

/**
 * Created by lashket on 16.5.16.
 */
public class GetPhotoFromCameraEvent {

    private Bitmap bitmap;

    public GetPhotoFromCameraEvent(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
