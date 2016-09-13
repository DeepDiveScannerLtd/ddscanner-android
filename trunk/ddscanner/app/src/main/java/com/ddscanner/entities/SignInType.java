package com.ddscanner.entities;

public enum SignInType {

    GOOGLE("google"), FACEBOOK("facebook");

    private String name;

    SignInType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
