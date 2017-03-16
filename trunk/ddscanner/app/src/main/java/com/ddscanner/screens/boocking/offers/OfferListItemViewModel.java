package com.ddscanner.screens.boocking.offers;

import com.ddscanner.entities.Offer;

public class OfferListItemViewModel {

    private Offer offer;

    public OfferListItemViewModel(Offer offer) {
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }

}
