package com.ddscanner.events;

public class LanguageChangedEvent {

    private String language;

    public LanguageChangedEvent(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}
