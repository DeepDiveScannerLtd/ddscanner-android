package com.ddscanner.events;

public class LanguageChosedEvent {

    private String languageName;

    public LanguageChosedEvent(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageName() {
        return languageName;
    }
}
