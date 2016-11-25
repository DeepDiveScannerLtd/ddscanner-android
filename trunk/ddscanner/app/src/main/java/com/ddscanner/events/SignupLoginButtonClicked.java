package com.ddscanner.events;

public class SignupLoginButtonClicked {

    private boolean isShowing;

    public SignupLoginButtonClicked(Boolean isShowing) {
        this.isShowing = isShowing;
    }

    public boolean isShowing() {
        return isShowing;
    }

}
