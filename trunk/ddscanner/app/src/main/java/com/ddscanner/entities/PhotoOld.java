package com.ddscanner.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoOld implements Parcelable{

    private String name;
    private boolean isReport;
    private UserOld userOld;

    protected PhotoOld(Parcel in) {
        name = in.readString();
        userOld = in.readParcelable(UserOld.class.getClassLoader());
        isReport = in.readByte() != 0;
    }



    public static final Creator<PhotoOld> CREATOR = new Creator<PhotoOld>() {
        @Override
        public PhotoOld createFromParcel(Parcel in) {
            return new PhotoOld(in);
        }

        @Override
        public PhotoOld[] newArray(int size) {
            return new PhotoOld[size];
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
