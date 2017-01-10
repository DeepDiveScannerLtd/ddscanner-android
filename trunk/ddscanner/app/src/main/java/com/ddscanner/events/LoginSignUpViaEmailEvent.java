package com.ddscanner.events;

public class LoginSignUpViaEmailEvent {

    private String email;
    private String password;
    private String userType;
    private boolean isSignUp;
    private String name;

    public LoginSignUpViaEmailEvent(String email, String password, String userType, boolean isSignUp, String name) {
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.isSignUp = isSignUp;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSignUp() {
        return isSignUp;
    }

    public String getUserType() {
        return userType;
    }

    public String getName() {
        return name;
    }
}
