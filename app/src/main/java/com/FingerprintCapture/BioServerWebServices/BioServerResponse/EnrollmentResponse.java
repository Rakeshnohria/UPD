package com.FingerprintCapture.BioServerWebServices.BioServerResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lakshmi on 28/05/18.
 */

public class EnrollmentResponse implements Serializable {
    @Expose
    @SerializedName("result")
    public Boolean mResult;
    @Expose
    @SerializedName("error")
    public ErrorResponse mError;

    public Boolean getmResult() {
        return mResult;
    }

    public void setmResult(Boolean mResult) {
        this.mResult = mResult;
    }

    public ErrorResponse getmError() {
        return mError;
    }

    public void setmError(ErrorResponse mError) {
        this.mError = mError;
    }
}
