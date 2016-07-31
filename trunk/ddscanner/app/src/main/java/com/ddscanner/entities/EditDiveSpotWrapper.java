package com.ddscanner.entities;

import java.util.List;

/**
 * Created by lashket on 29.7.16.
 */
public class EditDiveSpotWrapper {

    private EditDiveSpotEntity divespot;
    private List<Sealife> sealifes;

    public EditDiveSpotEntity getDivespot() {
        return divespot;
    }

    public void setDivespot(EditDiveSpotEntity divespot) {
        this.divespot = divespot;
    }

    public List<Sealife> getSealifes() {
        return sealifes;
    }

    public void setSealifes(List<Sealife> sealifes) {
        this.sealifes = sealifes;
    }
}
