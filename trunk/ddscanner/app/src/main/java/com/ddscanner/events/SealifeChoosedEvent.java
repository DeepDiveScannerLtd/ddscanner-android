package com.ddscanner.events;

import com.ddscanner.entities.Sealife;

/**
 * Created by lashket on 6.5.16.
 */
public class SealifeChoosedEvent {

    private Sealife sealife;

    public SealifeChoosedEvent(Sealife sealife) {
        this.sealife = sealife;
    }

    public Sealife getSealife() {
        return sealife;
    }

}
