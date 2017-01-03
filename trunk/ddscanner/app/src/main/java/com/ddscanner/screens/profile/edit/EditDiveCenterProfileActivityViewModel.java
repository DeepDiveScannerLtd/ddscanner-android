package com.ddscanner.screens.profile.edit;

import com.ddscanner.entities.DiveCenterProfile;

public class EditDiveCenterProfileActivityViewModel {

    private DiveCenterProfile diveCenterProfile;

    public EditDiveCenterProfileActivityViewModel(DiveCenterProfile diveCenterProfile) {
        this.diveCenterProfile = diveCenterProfile;
    }

    public DiveCenterProfile getDiveCenterProfile() {
        return diveCenterProfile;
    }
}
