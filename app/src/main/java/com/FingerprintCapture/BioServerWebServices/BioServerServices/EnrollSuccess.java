package com.FingerprintCapture.BioServerWebServices.BioServerServices;

import com.FingerprintCapture.models.response.UploadImageResponse;

public interface EnrollSuccess {
    void fail();
    void success(UploadImageResponse uploadImageResponse);
}
