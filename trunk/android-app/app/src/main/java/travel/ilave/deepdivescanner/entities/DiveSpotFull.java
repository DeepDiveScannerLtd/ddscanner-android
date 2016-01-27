package travel.ilave.deepdivescanner.entities;

import java.util.List;

/**
 * Created by lashket on 25.1.16.
 */
public class DiveSpotFull {

    private int id;
    private String name;
    private String description;
    private float lat;
    private float lng;
    private int rating;
    private String depth;
    private String visibility;
    private String currents;
    private String level;
    private List<String> images;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public float getLat() { return lat; }

    public void setLat(Float lat) { this.lat = lat; }

    public float getLng() { return lng; }

    public void setLng(Float lng) { this.lng = lng; }

    public int getRating() { return rating; }

    public void setRating(int rating) { this.rating = rating; }

    public String getDepth() { return depth; }

    public void setDepth(String depth) { this.depth = depth; }

    public String getVisibility() { return visibility; }

    public void setVisibility(String visibility) { this.visibility = visibility; }

    public String getCurrents() { return currents; }

    public void setCurrents(String currents) { this.currents = currents; }

    public String getLevel() { return level; }

    public void setLevel(String level) { this.level = level; }

    public List<String> getImages() { return images; }

    public void setImages(List<String> images) { this.images = images; }
}
