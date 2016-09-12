package com.ddscanner.entities.request;

/**
 * Created by lashket on 23.5.16.
 */
public class ValidationReguest {

    private String appId;
    private String social;
    private String token;
    private String secret;
    private String push;
    private boolean isValid;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

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
