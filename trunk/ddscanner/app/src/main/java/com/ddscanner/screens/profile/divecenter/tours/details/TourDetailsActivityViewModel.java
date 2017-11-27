package com.ddscanner.screens.profile.divecenter.tours.details;


import com.ddscanner.entities.DailyTourDetails;

public class TourDetailsActivityViewModel {

    private DailyTourDetails dailyTourDetails;

    public TourDetailsActivityViewModel(DailyTourDetails dailyTourDetails) {
        this.dailyTourDetails = dailyTourDetails;
    }

    public DailyTourDetails getDailyTourDetails() {
        return dailyTourDetails;
    }
}
