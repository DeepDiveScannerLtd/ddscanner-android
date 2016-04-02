package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lashket on 10.2.16.
 */
public class RetrofitError {
    @SerializedName("code")
    public int code;
    @SerializedName("error")
    public String errorDetails;
}
