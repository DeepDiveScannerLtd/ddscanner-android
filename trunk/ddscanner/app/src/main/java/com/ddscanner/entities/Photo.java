package com.ddscanner.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable{

    private String name;
    private boolean isReport;
    private UserOld userOld;

    protected Photo(Parcel in) {
        name = in.readString();
        userOld = in.readParcelable(UserOld.class.getClassLoader());
        isReport = in.readByte() != 0;
    }



    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeParcelable(userOld, i);
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

    public UserOld getUserOld() {
        return userOld;
    }

    public void setUserOld(UserOld userOld) {
        this.userOld = userOld;
    }
}
