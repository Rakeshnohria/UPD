package com.FingerprintCapture.models.request;

import com.google.gson.annotations.SerializedName;


public class UserRequest {

    @SerializedName("cust_id")
    private String custId;
    @SerializedName("pageIndex")
    private String pageIndex;
    @SerializedName("pageSize")
    private String pageSize;
    @SerializedName("search_name")
    private String seachName;


    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getSeachName() {
        return seachName;
    }

    public void setSeachName(String seachName) {
        this.seachName = seachName;
    }

}
