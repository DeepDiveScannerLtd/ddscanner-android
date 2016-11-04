package com.ddscanner.entities;

/**
 * Created by lashket on 10.3.16.
 */
public class RegisterUser {

    private String message;
    private UserOld userOld;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserOld getUserOld() {
        return userOld;
    }

    public void setUserOld(UserOld userOld) {
        this.userOld = userOld;
    }
}
