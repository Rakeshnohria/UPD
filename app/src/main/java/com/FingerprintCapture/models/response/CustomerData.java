package com.FingerprintCapture.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CustomerData implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("email")
    private String email;
    @SerializedName("finger_data")
    private List<UserFingerData> fingerData = null;
    @SerializedName("agent_id")
    private String agentId;
    @SerializedName("dob")
    private String dob;
    @SerializedName("upd_id")
    private String updId;
    @SerializedName("verified_user")
    private int verifiedUser;
    @SerializedName("fingerprint")
    private int fingerPrint;

    protected CustomerData(Parcel in) {
        id = in.readString();
        name = in.readString();
        firstName = in.readString();
        email = in.readString();
        fingerData = in.createTypedArrayList(UserFingerData.CREATOR);
        agentId = in.readString();
        dob = in.readString();
        updId = in.readString();
        verifiedUser = in.readInt();
        fingerPrint = in.readInt();
    }

    public static final Creator<CustomerData> CREATOR = new Creator<CustomerData>() {
        @Override
        public CustomerData createFromParcel(Parcel in) {
            return new CustomerData(in);
        }

        @Override
        public CustomerData[] newArray(int size) {
            return new CustomerData[size];
        }
    };

    public int getFingerPrint() { return fingerPrint; }
    public void setFingerPrint(int fingerPrint) { this.fingerPrint = fingerPrint; }
    public String getUpdId() {
        return updId;
    }

    public void setUpdId(String updId) {
        this.updId = updId;
    }

    public int getVerifiedUser() {
        return verifiedUser;
    }

    public void setVerifiedUser(int verifiedUser) {
        this.verifiedUser = verifiedUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<UserFingerData> getFingerData() {
        return fingerData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFingerData(List<UserFingerData> fingerData) {
        this.fingerData = fingerData;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(firstName);
        parcel.writeString(email);
        parcel.writeTypedList(fingerData);
        parcel.writeString(agentId);
        parcel.writeString(dob);
        parcel.writeString(updId);
        parcel.writeInt(verifiedUser);
        parcel.writeInt(fingerPrint);
    }
}
