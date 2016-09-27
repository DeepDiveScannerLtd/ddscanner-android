package com.ddscanner.entities;

public enum SignInType {

    GOOGLE("go"), FACEBOOK("fb");

    private String name;

    SignInType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
