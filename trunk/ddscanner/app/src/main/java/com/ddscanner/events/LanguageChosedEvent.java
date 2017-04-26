package com.ddscanner.events;

import com.ddscanner.entities.Language;


public class LanguageChosedEvent {

    private Language languageName;

    public LanguageChosedEvent(Language languageName) {
        this.languageName = languageName;
    }

    public Language getLanguageName() {
        return languageName;
    }
}
