package travel.ilave.deepdivescanner.entities;

import java.util.List;

import travel.ilave.deepdivescanner.entities.Country;

/**
 * Created by Vitaly on 29.11.2015.
 */
public class Booking {
//    "bookingId": "655",
//            "price": 278.8,
//            "symbol": "$",
//            "countries": [
//    {
//        "id": "1",
//            "name": "Thailand"
//    },
//    {
//        "id": "2",
//            "name": "Malaysia"
//    }
//    ]
    private String bookingId;
    private float price;
    private String symbol;
    private List<Country> countries;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }
}
