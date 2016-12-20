package com.ddscanner.entities;

import java.io.Serializable;

public class Language implements Serializable{

    private String code;
    private String name;

    public Language(String code, String name) {
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
