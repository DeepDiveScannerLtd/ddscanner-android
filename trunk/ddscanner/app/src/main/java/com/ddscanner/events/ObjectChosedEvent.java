package com.ddscanner.events;

import com.ddscanner.entities.BaseIdNamePhotoEntity;

public class ObjectChosedEvent {

    private BaseIdNamePhotoEntity baseIdNamePhotoEntity;

    public ObjectChosedEvent(BaseIdNamePhotoEntity baseIdNamePhotoEntity) {
        this.baseIdNamePhotoEntity = baseIdNamePhotoEntity;
    }

    public BaseIdNamePhotoEntity getBaseIdNamePhotoEntity() {
        return baseIdNamePhotoEntity;
    }
}
