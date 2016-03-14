package travel.ilave.deepdivescanner.entities;

import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

/**
 * Created by lashket on 5.2.16.
 */
public class DiveCenter implements Parcelable, Serializable, ClusterItem {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String lat;
    private String lng;
    private String logo;
    private String address;
    private float rating;
    private LatLng latLng;

    protected DiveCenter(android.os.Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        lat = in.readString();
        lng = in.readString();
        logo = in.readString();
        rating = in.readFloat();
        address = in.readString();
    }

    public static final Creator<DiveCenter> CREATOR = new Creator<DiveCenter>() {
        @Override
        public DiveCenter createFromParcel(android.os.Parcel in) {
            return new DiveCenter(in);
        }

        @Override
        public DiveCenter[] newArray(int size) {
            return new DiveCenter[size];
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(logo);
        dest.writeString(address);
        dest.writeFloat(rating);
    }

    @Override
    public LatLng getPosition() {
        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
            latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        }
        return latLng;
    }
}
