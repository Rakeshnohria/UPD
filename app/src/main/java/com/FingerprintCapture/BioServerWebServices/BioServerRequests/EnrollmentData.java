package com.FingerprintCapture.BioServerWebServices.BioServerRequests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshmi on 28/05/18.
 */

public class EnrollmentData implements Serializable{

    @SerializedName("personId")
    public String mFingerPrintId;
    @SerializedName("taxonomy")
    public String mTaxonomy;
    @SerializedName("biolocation")
    public String mBioLocation;
    @SerializedName("data")
    public String mFingerPrintImage;

    public String getmFingerPrintId() {
        return mFingerPrintId;
    }

    public void setmFingerPrintId(String mFingerPrintId) {
        this.mFingerPrintId = mFingerPrintId;
    }

    public String getmTaxonomy() {
        return mTaxonomy;
    }

    public void setmTaxonomy(String mTaxonomy) {
        this.mTaxonomy = mTaxonomy;
    }

    public String getmBioLocation() {
        return mBioLocation;
    }

    public void setmBioLocation(String mBioLocation) {
        this.mBioLocation = mBioLocation;
    }

    public String getmFingerPrintImage() {
        return mFingerPrintImage;
    }

    public void setmFingerPrintImage(String mFingerPrintImage) {
        this.mFingerPrintImage = mFingerPrintImage;
    }
}
