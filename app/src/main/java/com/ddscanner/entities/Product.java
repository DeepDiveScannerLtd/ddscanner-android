package com.ddscanner.entities;

import android.os.Parcelable;

import org.parceler.Parcel;

/**
 * Created by Vitaly on 28.11.2015.
 */
@Parcel
public class Product implements Parcelable {
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
    private String id;
    private String name;
    private String description;
    private int rating;
   // private float price;
   // private String symbol;
    private String lat;
    private String lng;
   // private List<String> images;

    protected Product(android.os.Parcel in) {
        id = in.readString();
        description = in.readString();
        rating = in.readInt();
    //    price = in.readFloat();
     //   symbol = in.readString();
        lat = in.readString();
        lng = in.readString();

    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(android.os.Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

  /*  public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
*/
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


  /*  public float getPrice() {
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
*/

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeInt(rating);
        //dest.writeFloat(price);
        //dest.writeString(symbol);
        dest.writeString(lat);
        dest.writeString(lng);
    }
}
