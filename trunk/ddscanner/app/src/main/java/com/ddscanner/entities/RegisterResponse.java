package com.ddscanner.entities;

import java.io.Serializable;

/**
 * Created by lashket on 23.3.16.
 */
public class RegisterResponse implements Serializable {
    private String message;
    private User user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
