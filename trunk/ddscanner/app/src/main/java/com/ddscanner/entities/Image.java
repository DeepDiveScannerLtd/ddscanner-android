package com.ddscanner.entities;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lashket on 22.7.16.
 */
public class Image implements Parcelable {

    private String name;
    private Author author;
    private boolean isReport;

    protected Image(Parcel in) {
        name = in.readString();
        author = in.readParcelable(Author.class.getClassLoader());
        isReport = in.readByte() != 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(android.os.Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeParcelable(author, i);
        parcel.writeByte((byte) (isReport ? 1 : 0));
    }

    public boolean isReport() {
        return isReport;
    }

    public void setReport(boolean report) {
        isReport = report;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
