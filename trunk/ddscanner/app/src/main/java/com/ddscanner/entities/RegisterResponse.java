package com.ddscanner.entities;

import java.io.Serializable;

/**
 * Created by lashket on 23.3.16.
 */
public class RegisterResponse implements Serializable {
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
