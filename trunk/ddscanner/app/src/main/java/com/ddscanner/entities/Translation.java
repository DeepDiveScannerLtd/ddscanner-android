package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class Translation {

    @SerializedName("content_lang")
    private String code;
    @SerializedName("lang_name")
    private String language;
    private String name;
    private String description;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Language getLanguageEntity() {
        Language language =  new Language(code, name);
        return language;
    }

}
