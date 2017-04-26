package com.ddscanner.events;

import com.ddscanner.entities.UserOld;

public class ShowUserDialogEvent {

    private UserOld userOld;

    public ShowUserDialogEvent(UserOld userOld) {
        this.userOld = userOld;
    }

    public UserOld getUserOld() {
        return this.userOld;
    }
}
