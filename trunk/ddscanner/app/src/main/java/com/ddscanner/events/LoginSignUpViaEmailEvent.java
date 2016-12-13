package com.ddscanner.events;

public class LoginSignUpViaEmailEvent {

    private String email;
    private String password;
    private String userType;
    private boolean isSignUp;

    public LoginSignUpViaEmailEvent(String email, String password, String userType, boolean isSignUp) {
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.isSignUp = isSignUp;
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
}
