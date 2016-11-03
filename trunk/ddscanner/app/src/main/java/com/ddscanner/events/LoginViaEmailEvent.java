package com.ddscanner.events;

public class LoginViaEmailEvent {

    private String email;
    private String password;
    private String userType;

    public LoginViaEmailEvent(String email, String password, String userType) {
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
