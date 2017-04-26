package com.ddscanner.screens.divecemter.profile.languages;

import com.ddscanner.entities.Language;

public class DiveCenterLanguageItemViewModel {

    private Language language;

    public DiveCenterLanguageItemViewModel(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }
}
