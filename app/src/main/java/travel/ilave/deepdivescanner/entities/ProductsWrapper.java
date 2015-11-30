package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class ProductsWrapper {
//    "license": "OWD",
//            "products": [
//    {
//        "id": "95",
//            "name": "Omnis harum ",
//            "description": "Aspernatur inventore ut",
//            "rating": 3,
//            "hotOffers": false,
//            "price": 6.85,
//            "hotPrice": 2.52,
//            "symbol": "$",
//            "lat": "-36.4755180000",
//            "lng": "123.5373760000"
//    },
//            ]

    private String license;
    private List<Product> products;

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
