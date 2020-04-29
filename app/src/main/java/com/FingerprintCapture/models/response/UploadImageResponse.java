package com.FingerprintCapture.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadImageResponse implements Parcelable {
    private int status;
    private String message;
    @SerializedName("data")
    private List<CustomerData> customerData;

    private UploadImageResponse(Parcel in) {
        status = in.readInt();
        message = in.readString();
        customerData = in.createTypedArrayList(CustomerData.CREATOR);
    }

    public static final Creator<UploadImageResponse> CREATOR = new Creator<UploadImageResponse>() {
        @Override
        public UploadImageResponse createFromParcel(Parcel in) {
            return new UploadImageResponse(in);
        }

        @Override
        public UploadImageResponse[] newArray(int size) {
            return new UploadImageResponse[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CustomerData> getCustomerData() {
        return customerData;
    }

    public void setCustomerData(List<CustomerData> customerData) {
        this.customerData = customerData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(message);
        dest.writeTypedList(customerData);
    }
}
