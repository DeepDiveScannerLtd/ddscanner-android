package com.ddscanner.entities.request;

import java.util.HashMap;

public class DiveSpotsRequestMap extends HashMap<String, Object> {

    public static final String KEY_CURRENTS = "currents";
    public static final String KEY_LEVEL = "diving_skill";
    public static final String KEY_OBJECT = "type";
    public static final String KEY_RATING = "rating";
    public static final String KEY_VISIBILITY = "visibility";
    public static final String KEY_COUNTRY_CODE = "country_code";

    /*
currents (string, optional) - The currents filter
latLeft (float, required) - The latitude lower left corner in area
latRight (float, required) - The latitude upper right corner in area
level (string, optional) - The level filter
latRight (float, required) - The longitude lower left corner in area
lngRight (float, required) - The longitude upper right corner in area
object (string, optional) - The object filter
rating (integer, optional) - The raring filter
visibility (string, optional) - The Visibility filter
     */

    public void putCountryCode(String countryCode) {
        put(KEY_COUNTRY_CODE, countryCode);
    }

    public String getKeyCountryCode() {
       return (String) get(KEY_COUNTRY_CODE);
    }

    public void putCurrents(String currents) {
        put(KEY_CURRENTS, currents);
    }

    public String getCurrents() {
        return (String) get(KEY_CURRENTS);
    }

    public void putLevel(String level) {
        put(KEY_LEVEL, level);
    }

    public String getLevel() {
        return (String) get(KEY_LEVEL);
    }

    public void putObject(String object) {
        put(KEY_OBJECT, object);
    }

    public String getObject() {
        return (String) get(KEY_OBJECT);
    }

    public void putRating(int rating) {
        put(KEY_RATING, rating);
    }

    public int getRating() {
        return (int) get(KEY_RATING);
    }

    public void putVisibility(String visibility) {
        put(KEY_VISIBILITY, visibility);
    }

    public String getVisibility() {
        return (String) get(KEY_VISIBILITY);
    }

    public void putSouthWestLat(double lat) {
        put("lat_bottom", String.valueOf(lat));
    }

    public void putSouthWestLng(double lng) {
        put("lng_left", String.valueOf(lng));
    }

    public void putNorthEastLat(double lat) {
        put("lat_top", String.valueOf(lat));
    }

    public void putNorthEastLng(double lng) {
        put("lng_right", String.valueOf(lng));
    }

    public double getSouthWestLat() {
        return Double.valueOf((String) get("lat_bottom"));
    }

    public double getSouthWestLng() {
        return Double.valueOf((String) get("lng_left"));
    }

    public double getNorthEastLat() {
        return Double.valueOf((String) get("lat_top"));
    }

    public double getNorthEastLng() {
        return Double.valueOf((String) get("lng_right"));
    }
}
