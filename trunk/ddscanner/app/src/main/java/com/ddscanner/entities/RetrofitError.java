package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class RetrofitError {
    @SerializedName("code")
    public int code;
    @SerializedName("error")
    public String errorDetails;
}
