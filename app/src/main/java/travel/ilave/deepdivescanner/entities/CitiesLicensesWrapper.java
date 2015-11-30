package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class CitiesLicensesWrapper {
//    "cities": [
//    {
//        "id": "1",
//            "name": "Bangkok"
//    },
//    {
//        "id": "2",
//            "name": "Phuket"
//    },
//            ],
//            "licences": [
//            "OWD",
//            "AWD",
//            "DM"
//            ]

    private List<City> cities;
    private List<String> licences;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<String> getLicences() {
        return licences;
    }

    public void setLicences(List<String> licences) {
        this.licences = licences;
    }
}
