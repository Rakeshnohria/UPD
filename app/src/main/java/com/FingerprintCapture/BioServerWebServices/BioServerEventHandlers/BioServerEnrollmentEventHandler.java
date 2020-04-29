package com.FingerprintCapture.BioServerWebServices.BioServerEventHandlers;


import android.app.Activity;
import android.util.Log;

import com.FingerprintCapture.BioServerWebServices.BioServerRequests.EnrollmentData;
import com.FingerprintCapture.BioServerWebServices.BioServerResponse.EnrollmentResponse;
import com.FingerprintCapture.BioServerWebServices.BioServerRestApiClientConfig.BioServerApiClientConfig;
import com.FingerprintCapture.BioServerWebServices.BioServerServices.EnrollFingerPrintService;
import com.FingerprintCapture.BioServerWebServices.BioServerServices.EnrollSuccess;
import com.FingerprintCapture.BioServerWebServices.Constants.Constants;
import com.FingerprintCapture.R;
import com.FingerprintCapture.UIHandlerUtilities.UIHandler;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.FingerprintCapture.models.response.UploadImageResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by Lakshmi on 28/05/18.
 */

//public class BioServerEnrollmentEventHandler {
//    private EnrollmentData mEnrollmentData;
//    private EnrollSuccess mEnrollSuccess;
//    /**
//     * Constructor with param pFingerprintId - personId
//     * param pFingerPrintImage - image64
//     */
//    public BioServerEnrollmentEventHandler(String pFingerprintId, String pFingerPrintImage,Activity pActivity) {
//        if (!pFingerprintId.isEmpty() && !pFingerPrintImage.isEmpty()) {
//            mEnrollmentData = new EnrollmentData();
//            mEnrollmentData.setmBioLocation(Constants.BioServerRestClientConfig.sBIOLOCATION);
//            mEnrollmentData.setmTaxonomy(Constants.BioServerRestClientConfig.sTaxonomy);
//            mEnrollmentData.setmFingerPrintId(pFingerprintId);
//            mEnrollmentData.setmFingerPrintImage(pFingerPrintImage);
//            mEnrollSuccess=(EnrollSuccess)pActivity;
////            Gson gson = new Gson();
////            Log.e("Bitmap: ", gson.toJson(mEnrollmentData));
//        }
//    }
//
//    /**
//     * RxJava Synchronization
//     * Request to BioServer from App to Api
//     */
//
//    public void doEnrollment(Activity pActivity, UploadImageResponse pUploadImageResponse) {
//
//        if (mEnrollmentData != null) {
//            FingerprintCaptureApplication.getApplicationInstance().showProgress(pActivity,"");
//             BioServerApiClientConfig apiClientConfig = new BioServerApiClientConfig();
//            Observable<Response<EnrollmentResponse>> observable=apiClientConfig.getRetrofit().create(EnrollFingerPrintService.class).doEnrollment(mEnrollmentData);
//        observable.timeout(Constants.sTIME_OUT, TimeUnit.SECONDS)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<Response<EnrollmentResponse>>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//                        }
//
//                        @Override
//                        public void onNext(Response<EnrollmentResponse> pEnrollmentResponse) throws NullPointerException {
//                            FingerprintCaptureApplication.getApplicationInstance().hideProgress();
//                            if (pEnrollmentResponse.body() != null) {
//                                if (pEnrollmentResponse.body().getmResult() != null) {
//                                    if (pEnrollmentResponse.body().getmResult()) {
//                                        Log.d("response of biometric", pEnrollmentResponse.body().mResult.toString());
//                                           mEnrollSuccess.success(pUploadImageResponse);
//                                    } else {
//                                        mEnrollSuccess.fail();
//                                    }
//                                } else if (pEnrollmentResponse.body().getmError() != null) {
//                                    UIHandler.showDialog(pActivity, pActivity.getResources().getString(R.string.response), pEnrollmentResponse.body().getmError().getmMessage());
//                                }
//                            }
//                            else
//                                Log.d("response of biometric", "null");
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            FingerprintCaptureApplication.getApplicationInstance().hideProgress();
//                            if (!pActivity.isFinishing()) {
//                                UIHandler.showingAlert(e.getMessage(), pActivity);
//                            }
//                            Log.e("bioserver R/failure", e.getMessage());
//                           mEnrollSuccess.fail();
//                        }
//
//                        @Override
//                        public void onComplete() {
//                            observable.unsubscribeOn(Schedulers.io());
//                        }
//                    });
//        }
//
//    }
//
//
//
//}
