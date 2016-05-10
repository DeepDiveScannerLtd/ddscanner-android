package com.ddscanner.events;

import com.ddscanner.entities.User;

public class ShowUserDialogEvent {

    private User user;

    public ShowUserDialogEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
