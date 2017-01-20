package com.ddscanner.events;

import com.ddscanner.entities.Translation;

public class ChangeTranslationEvent {

    private Translation translation;

    public ChangeTranslationEvent(Translation translation) {
        this.translation = translation;
    }

    public Translation getTranslation() {
        return translation;
    }
}
