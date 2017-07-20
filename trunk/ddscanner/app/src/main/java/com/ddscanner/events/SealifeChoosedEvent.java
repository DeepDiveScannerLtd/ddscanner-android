package com.ddscanner.events;

import com.ddscanner.entities.SealifeShort;

public class SealifeChoosedEvent {

    private SealifeShort sealife;

    public SealifeChoosedEvent(SealifeShort sealife) {
        this.sealife = sealife;
    }

    public SealifeShort getSealife() {
        return sealife;
    }

}
