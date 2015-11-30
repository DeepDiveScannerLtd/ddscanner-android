package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class OfferWrapper {
//    "options": [
//    {
//        "id": "Full day trip",
//            "duration": "10 hours",
//            "meetingPoint": "No",
//            "inclusions": "Full board (light breakfast, buffet lunch, snacks, fruits, coffee, tea, soft drinks), guid",
//            "price": "100.00",
//            "hotOffers": false,
//            "hotPrice": null,
//            "symbol": "$"
//    },
//    {
//        "id": "2 days trip",
//            "duration": "36 hours",
//            "meetingPoint": "No",
//            "inclusions": "Transfers (Kata, Karon, Patong area)",
//            "price": "150.00",
//            "hotOffers": false,
//            "hotPrice": null,
//            "symbol": "$"
//    }
//    ]
    private List<Offer> options;

    public List<Offer> getOptions() {
        return options;
    }

    public void setOptions(List<Offer> options) {
        this.options = options;
    }
}
