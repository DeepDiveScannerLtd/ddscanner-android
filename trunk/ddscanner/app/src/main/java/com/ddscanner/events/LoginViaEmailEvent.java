package com.ddscanner.events;

public class LoginViaEmailEvent {

    private String email;
    private String password;
    private String userType;
    private boolean isRegister;

    public LoginViaEmailEvent(String email, String password, String userType, boolean isRegister) {
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.isRegister = isRegister;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public String getUserType() {
        return userType;
    }
}
