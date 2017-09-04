package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class SignUpResponseEntity {

    private String token;
    private String id;
    @SerializedName("user_type")
    private int type;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
