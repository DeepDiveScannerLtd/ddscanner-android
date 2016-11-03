package com.ddscanner.events;

public class LoginViaEmailEvent {

    private String email;
    private String password;

    public LoginViaEmailEvent(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
