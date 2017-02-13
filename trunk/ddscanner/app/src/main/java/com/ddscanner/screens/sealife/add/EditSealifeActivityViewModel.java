package com.ddscanner.screens.sealife.add;

import com.ddscanner.entities.Sealife;

public class EditSealifeActivityViewModel {

    private Sealife sealife;

    public EditSealifeActivityViewModel(Sealife sealife) {
        this.sealife = sealife;
    }

    public Sealife getSealife() {
        return sealife;
    }
}
