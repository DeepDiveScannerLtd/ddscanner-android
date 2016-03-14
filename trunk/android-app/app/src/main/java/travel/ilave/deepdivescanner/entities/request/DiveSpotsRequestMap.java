package travel.ilave.deepdivescanner.entities.request;

import java.util.HashMap;

public class DiveSpotsRequestMap extends HashMap<String, Object> {

    public static final String KEY_CURRENTS = "currents";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_OBJECT = "object";
    public static final String KEY_RATING = "rating";
    public static final String KEY_VISIBILITY = "visibility";

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
        put("latLeft", String.valueOf(lat));
    }

    public void putSouthWestLng(double lng) {
        put("lngLeft", String.valueOf(lng));
    }

    public void putNorthEastLat(double lat) {
        put("latRight", String.valueOf(lat));
    }

    public void putNorthEastLng(double lng) {
        put("lngRight", String.valueOf(lng));
    }

    public double getSouthWestLat() {
        return Double.valueOf((String) get("latLeft"));
    }

    public double getSouthWestLng() {
        return Double.valueOf((String) get("lngLeft"));
    }

    public double getNorthEastLat() {
        return Double.valueOf((String) get("latRight"));
    }

    public double getNorthEastLng() {
        return Double.valueOf((String) get("lngRight"));
    }
}
