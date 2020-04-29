package com.FingerprintCapture.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by chicmic on 10/31/18.
 */

public class UserData {

    @SerializedName("row_count")
    private int rowCount;
    @SerializedName("customer_data")
    private ArrayList<CustomerData> customerData;
    @SerializedName("is_subscribed_user")
    private int isSubscribedUser;

    public int getIsSubscribedUser() { return isSubscribedUser; }

    public void setIsSubscribedUser(int isSubscribedUser) { this.isSubscribedUser = isSubscribedUser; }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public ArrayList<CustomerData> getCustomerData() {
        return customerData;
    }

    public void setCustomerData(ArrayList<CustomerData> customerData) {
        this.customerData = customerData;
    }
}
