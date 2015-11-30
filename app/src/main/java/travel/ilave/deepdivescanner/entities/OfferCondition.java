package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class OfferCondition {
//    "id": "8",
//            "name": "Full day trip",
//            "duration": "10 hours",
//            "pickups": [
//    {
//        "id": "25",
//            "time": "08:00:00",
//            "location": "TriTrang Hotel"
//    },
//    {
//        "id": "26",
//            "time": "08:15:00",
//            "location": "Aloha Villa"
//    },
//            ],
//            "prices": [
//    {
//        "id": "10",
//            "type": "Abult",
//            "price": "100.00",
//            "description": "+18",
//            "symbol": "$"
//    }
//    ],
//            "languages": [
//    {
//        "id": "1",
//            "name": "Abkhaz"
//    },
//    {
//        "id": "2",
//            "name": "Afar"
//    },
//            ]
    private String id;
    private String name;
    private String duration;
    private List<PickUp> pickups;
    private List<ConditionPrice> prices;
    private List<ConditionLanguage> languages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<PickUp> getPickups() {
        return pickups;
    }

    public void setPickups(List<PickUp> pickups) {
        this.pickups = pickups;
    }

    public List<ConditionPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<ConditionPrice> prices) {
        this.prices = prices;
    }

    public List<ConditionLanguage> getLanguages() {
        return languages;
    }

    public void setLanguages(List<ConditionLanguage> languages) {
        this.languages = languages;
    }
}
