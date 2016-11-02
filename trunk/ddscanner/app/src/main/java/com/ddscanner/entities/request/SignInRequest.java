package com.ddscanner.entities.request;

public class SignInRequest extends SignUpRequest {

    private int provider_type;
    private String token;

    public int getProvider_type() {
        return provider_type;
    }

    public void setProvider_type(int provider_type) {
        this.provider_type = provider_type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
