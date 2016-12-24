package com.ddscanner.entities;

import java.io.Serializable;

public class DiveCenterCountry implements Serializable {

    private String code;
    private String name;

    public DiveCenterCountry(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
