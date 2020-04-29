package com.FingerprintCapture.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserFingerData implements Parcelable {

    @SerializedName("thumb-image")
    private String thumbImage;
    @SerializedName("main-image")
    private String mainImage;
    @SerializedName("created_at")
    private String createdAt;

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumbImage);
        dest.writeString(this.mainImage);
        dest.writeString(this.createdAt);
    }

    public UserFingerData() {
    }

    protected UserFingerData(Parcel in) {
        this.thumbImage = in.readString();
        this.mainImage = in.readString();
        this.createdAt = in.readString();
    }

    public static final Parcelable.Creator<UserFingerData> CREATOR = new Parcelable.Creator<UserFingerData>() {
        @Override
        public UserFingerData createFromParcel(Parcel source) {
            return new UserFingerData(source);
        }

        @Override
        public UserFingerData[] newArray(int size) {
            return new UserFingerData[size];
        }
    };
}
