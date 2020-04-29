package com.FingerprintCapture.BioServerWebServices.BioServerResponse;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshmi on 28/05/18.
 */

public class ErrorResponse implements Serializable {
    @SerializedName("message")
    public String mMessage;
    @SerializedName("code")
    public String mCode;

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }
}
