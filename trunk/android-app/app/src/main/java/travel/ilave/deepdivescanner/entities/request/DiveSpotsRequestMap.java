package travel.ilave.deepdivescanner.entities.request;

import java.util.HashMap;

public class DiveSpotsRequestMap extends HashMap<String, String> {
    
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
        return Double.valueOf(get("latLeft"));
    }

    public double getSouthWestLng() {
        return Double.valueOf(get("lngLeft"));
    }

    public double getNorthEastLat() {
        return Double.valueOf(get("latRight"));
    }

    public double getNorthEastLng() {
        return Double.valueOf( get("lngRight"));
    }
}
