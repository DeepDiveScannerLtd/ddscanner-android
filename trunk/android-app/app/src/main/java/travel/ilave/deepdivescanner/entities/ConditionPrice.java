package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class ConditionPrice {
    //        "id": "10",
//            "type": "Abult",
//            "price": "100.00",
//            "description": "+18",
//            "symbol": "$"
//    }
    private String id;
    private String type;
    private String price;
    private String description;
    private String symbol;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
