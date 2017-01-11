package com.ddscanner.events;

import com.ddscanner.entities.Language;

import org.apache.commons.codec.language.bm.Lang;

public class LanguageChosedEvent {

    private Language languageName;

    public LanguageChosedEvent(Language languageName) {
        this.languageName = languageName;
    }

    public Language getLanguageName() {
        return languageName;
    }
}
