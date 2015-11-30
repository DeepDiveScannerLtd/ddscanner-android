package travel.ilave.deepdivescanner.entities;

import android.os.Parcelable;

import org.parceler.Parcel;

import java.util.ArrayList;

import travel.ilave.deepdivescanner.entities.Count;
import travel.ilave.deepdivescanner.entities.Price;

/**
 * Created by Admin on 29.11.2015.
 */
@Parcel
public class Traveller implements Parcelable{
    private String bookingId;
    private String optionName;
    private String duration;
    private String firstName;
    private String lastName;
    private String date;
    private String note;
    private PriceList prices;
    private CountList counts;

    protected Traveller(android.os.Parcel in) {
        bookingId = in.readString();
        optionName = in.readString();
        duration = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        date = in.readString();
        note = in.readString();
    }

    public static final Creator<Traveller> CREATOR = new Creator<Traveller>() {
        @Override
        public Traveller createFromParcel(android.os.Parcel in) {
            return new Traveller(in);
        }

        @Override
        public Traveller[] newArray(int size) {
            return new Traveller[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(bookingId);
        dest.writeString(optionName);
        dest.writeString(duration);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(date);
        dest.writeString(note);
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public PriceList getPrices() {
        return prices;
    }

    public void setPrices(PriceList prices) {
        this.prices = prices;
    }

    public CountList getCounts() {
        return counts;
    }

    public void setCounts(CountList counts) {
        this.counts = counts;
    }

    public static class PriceList extends ArrayList<Price> {
    }
    public static class CountList extends ArrayList<Count> {
    }
}
