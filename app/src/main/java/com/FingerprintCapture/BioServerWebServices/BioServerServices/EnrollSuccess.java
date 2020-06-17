package com.FingerprintCapture.BioServerWebServices.BioServerServices;

import com.FingerprintCapture.models.response.UploadImageResponse;

public interface EnrollSuccess {
    void fail(String message);
    void success(UploadImageResponse uploadImageResponse);
}
