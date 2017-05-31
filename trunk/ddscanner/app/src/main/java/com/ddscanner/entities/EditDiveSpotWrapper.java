package com.ddscanner.entities;

import java.util.List;

public class EditDiveSpotWrapper {

    private EditDiveSpotEntity divespot;
    private List<SealifeShort> sealifes;

    public EditDiveSpotEntity getDivespot() {
        return divespot;
    }

    public void setDivespot(EditDiveSpotEntity divespot) {
        this.divespot = divespot;
    }

    public List<SealifeShort> getSealifes() {
        return sealifes;
    }

    public void setSealifes(List<SealifeShort> sealifes) {
        this.sealifes = sealifes;
    }
}
