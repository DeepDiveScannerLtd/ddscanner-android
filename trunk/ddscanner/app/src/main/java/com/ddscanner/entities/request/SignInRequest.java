package com.ddscanner.entities.request;

import com.google.gson.annotations.SerializedName;

public class SignInRequest extends SignUpRequest {

    @SerializedName("provider_type")
    private Integer providerType;
    private String token;

    public Integer getProviderType() {
        return providerType;
    }

    public void setProviderType(Integer providerType) {
        this.providerType = providerType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
