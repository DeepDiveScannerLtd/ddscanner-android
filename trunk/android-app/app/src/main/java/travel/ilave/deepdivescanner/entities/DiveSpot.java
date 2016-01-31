package travel.ilave.deepdivescanner.entities;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 20.1.16.
 */
public class DiveSpot implements Parcelable {
    private String id;
    private String name;
    private int rating;
    private String description;
    private String lat;
    private String lng;
    private List<String> images;

    protected DiveSpot(android.os.Parcel in) {
        id = in.readString();
        description = in.readString();
        rating = in.readInt();
        lat = in.readString();
        lng = in.readString();
    }

    public static final Creator<DiveSpot> CREATOR = new Creator<DiveSpot>() {
        @Override
        public DiveSpot createFromParcel(android.os.Parcel in) {
            return new DiveSpot(in);
        }

        @Override
        public DiveSpot[] newArray(int size) {
            return new DiveSpot[size];
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getLat() { return lat; }

    public void setLat(String lat) { this.lat = lat; }

    public String getLng() { return lng; }

    public void setLng(String lng) { this.lng = lng; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeList(images);
    }

}