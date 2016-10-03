package com.ddscanner.entities.request;

/**
 * Created by lashket on 11.3.16.
 */
public class RegisterRequest {

    protected String appId;
    protected String social;
    protected String token;
    protected String secret;
    protected String push;

    public String getpush() {
        return push;
    }

    public void setpush(String push) {
        this.push = push;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
