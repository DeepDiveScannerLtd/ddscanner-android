package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GeocodingResultEntity {

    @SerializedName("address_components")
    private ArrayList<AddressComponent> addressComponents;

    public ArrayList<AddressComponent> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(ArrayList<AddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }
}
