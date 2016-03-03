package travel.ilave.deepdivescanner.entities;

import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.List;

import travel.ilave.deepdivescanner.utils.LogUtils;

/**
 * Created by lashket on 20.1.16.
 */
public class DiveSpot implements Serializable, ClusterItem {

    private static final String TAG = DiveSpot.class.getName();
    private long id;
    private String name;
    private int rating;
    private String description;
    private String lat;
    private String lng;
    private List<String> images;
    private LatLng latLng;

   /* protected DiveSpot(android.os.Parcel in) {
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
*/


    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
        initLatLng();
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
        initLatLng();
    }

    public void initLatLng() {
        LogUtils.i(TAG, "initLatLng lat=" + lat + " lng=" + lng);
        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
            latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiveSpot diveSpot = (DiveSpot) o;

        return getId() == diveSpot.getId();

    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }
    /*  @Override
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
    }*/

}
