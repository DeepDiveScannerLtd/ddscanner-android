package travel.ilave.deepdivescanner.entities;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class PickUp {
    //    {
//        "id": "25",
//            "time": "08:00:00",
//            "location": "TriTrang Hotel"
//    }
    private String id;
    private String time;
    private String location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return location;
    }
}
