package com.ddscanner.events;

import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeShort;

/**
 * Created by lashket on 6.5.16.
 */
public class SealifeChoosedEvent {

    private SealifeShort sealife;

    public SealifeChoosedEvent(SealifeShort sealife) {
        this.sealife = sealife;
    }

    public SealifeShort getSealife() {
        return sealife;
    }

}
