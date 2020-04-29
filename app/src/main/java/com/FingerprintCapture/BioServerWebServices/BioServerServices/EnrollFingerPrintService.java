package com.FingerprintCapture.BioServerWebServices.BioServerServices;

import io.reactivex.Observable;

import com.FingerprintCapture.BioServerWebServices.BioServerRequests.EnrollmentData;
import com.FingerprintCapture.BioServerWebServices.BioServerResponse.EnrollmentResponse;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Lakshmi on 28/05/18.
 */

public interface EnrollFingerPrintService {

    @POST("rpc/api/image/enroll")
    Observable<Response<EnrollmentResponse>> doEnrollment(@Body EnrollmentData enrollmentData);
}
