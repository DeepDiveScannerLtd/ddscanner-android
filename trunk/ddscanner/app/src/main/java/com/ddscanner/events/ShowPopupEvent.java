package com.ddscanner.events;

import com.ddscanner.entities.Popup;

public class ShowPopupEvent {

    private String popup;

    public ShowPopupEvent(String popup) {
        this.popup = popup;
    }

    public String getPopup() {
        return popup;
    }
}
