package com.FingerprintCapture.BioServerWebServices.BioServerEventHandlers;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.FingerprintCapture.BioServerWebServices.BioServerRequests.EnrollmentData;
import com.FingerprintCapture.BioServerWebServices.BioServerResponse.EnrollmentResponse;
import com.FingerprintCapture.BioServerWebServices.BioServerRestApiClientConfig.BioServerApiClientConfig;
import com.FingerprintCapture.BioServerWebServices.BioServerServices.IdentifyFingerPrintService;
import com.FingerprintCapture.BioServerWebServices.Constants.Constants;
import com.FingerprintCapture.R;
import com.FingerprintCapture.Services.AllUrl;
import com.FingerprintCapture.Services.RestClient;
import com.FingerprintCapture.Services.UserApi;
import com.FingerprintCapture.UIHandlerUtilities.UIHandler;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.cunoraz.gifview.library.GifView;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;

/**
 * Created by Lakshmi on 28/05/18.
 */

public class BioServerIdentifyEventHandler {
    private EnrollmentData mEnrollmentData;
    private View mActivityAddFingerprintView;
    private boolean SignatureVerified = false;
    private Button mBtVerify, mBtScan;
    private GifView mGif;
    private FrameLayout mNotifyOverlay;
    private String mUserId = null;

    /**
     * Constructor with param pFingerprintID - personId
     * param pFingerPrintImage - image64
     */
    public BioServerIdentifyEventHandler(String pFingerprintID, String pFingerPrintImage, View pActivityAddFingerprintBinding ,View pNotifyOvrlay) {
        if (!pFingerprintID.isEmpty() && !pFingerPrintImage.isEmpty()) {
            mEnrollmentData = new EnrollmentData();
            mEnrollmentData.setmBioLocation(Constants.BioServerRestClientConfig.sBIOLOCATION);
            mEnrollmentData.setmTaxonomy(Constants.BioServerRestClientConfig.sTaxonomy);
            mEnrollmentData.setmFingerPrintId(pFingerprintID);
            mEnrollmentData.setmFingerPrintImage(pFingerPrintImage);
            mNotifyOverlay=(FrameLayout) pNotifyOvrlay;
            this.mActivityAddFingerprintView = pActivityAddFingerprintBinding;
            initiateComponents();
        }
    }

    private void initiateComponents() {
        mBtScan = mActivityAddFingerprintView.findViewById(R.id.bt_scan);
        mBtVerify = mActivityAddFingerprintView.findViewById(R.id.bt_verify);
    }

    /**
     * RxJava Synchronization
     * Request to BioServer to identify FingerPrint
     */
    public void doIdentification(Activity pActivity, String pId) {
        if (mEnrollmentData != null) {

            mUserId = pId;

            BioServerApiClientConfig apiClientConfig = new BioServerApiClientConfig();
            Observable<Response<EnrollmentResponse>> observable = apiClientConfig.getRetrofit().create(IdentifyFingerPrintService.class).identifyEnrollment(mEnrollmentData);
            observable.timeout(Constants.sTIME_OUT, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<EnrollmentResponse>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<EnrollmentResponse> pEnrollmentResponse) {
                            FingerprintCaptureApplication.getApplicationInstance().hideProgress();
                            if (pEnrollmentResponse.body() != null) {
                                if (pEnrollmentResponse.body().getmResult() != null) {
                                    if (pEnrollmentResponse.body().getmResult()) {
                                        Log.d("1stb",pEnrollmentResponse.body().mResult.toString() );
                                        onEnrollmentSucessResponse(pEnrollmentResponse.body(), pActivity);
                                    } else {
                                        Log.d("1st2ndb",pEnrollmentResponse.body().mResult.toString() );
                                        onEnrollmentFailResponse(pActivity, pActivity.getResources().getString(R.string.fingerprint_error));
                                    }
                                } else if (pEnrollmentResponse.body().getmError() != null) {
                                    UIHandler.showDialog(pActivity, pActivity.getResources().getString(R.string.response), pEnrollmentResponse.body().getmError().getmMessage());
                                }
                            }
                            else{
                                onEnrollmentFailResponse(pActivity,pActivity.getResources().getString(R.string.please_scan));

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            FingerprintCaptureApplication.getApplicationInstance().hideProgress();
                            if (!pActivity.isFinishing()) {
                                UIHandler.showingAlert(e.getMessage(), pActivity);
                            }
                            Log.d(getClass().getName(), e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            observable.unsubscribeOn(Schedulers.io());
                        }
                    });
        }
    }

    /**
     * on IdentificationSuccess
     */
    private void onEnrollmentSucessResponse(EnrollmentResponse pEnrollmentResponse, Activity pActivity) {
        SignatureVerified = true;

        /*
         * on success response of bioserver update data on server
         */
        Retrofit retrofit = RestClient.build(AllUrl.baseUrl);
        UserApi userApi = retrofit.create(UserApi.class);
        FingerprintCaptureApplication.getApplicationInstance().setUpdated(true);

        Call<JsonObject> call = userApi.updateVerified(mUserId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.body().get("status").toString().equals("200")) {
                    mActivityAddFingerprintView.setVisibility(View.GONE);
                    mNotifyOverlay.setVisibility(View.VISIBLE);
                    playNotifyGif(mNotifyOverlay.findViewById(R.id.responsegif), R.raw.ticka);
                    Log.d("1st",response.body().toString() );
                //    sucess(pActivity, pActivity.getResources().getString(R.string.verified_success));
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        Intent intent = new Intent();
                        intent.putExtra(com.FingerprintCapture.Utilities.Constants.Fingerprint.sVERIFIEDFINGER_PRINT, true);
                        pActivity.setResult(com.FingerprintCapture.Utilities.Constants.Fingerprint.sVerified, intent);
                        pActivity.finish();
                    }, 2500);
                } else {
                    Log.d("1st2nd",response.body().toString() );
                    onEnrollmentFailResponse(pActivity, pActivity.getResources().getString(R.string.fingerprint_error));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("1st2nd3rd",t.toString() );
                onEnrollmentFailResponse(pActivity, pActivity.getResources().getString(R.string.fingerprint_error));
            }
        });


    }

    private void sucess(Activity pActivity,String msg) {
        if (pActivity != null && !pActivity.isFinishing()) {
            AlertDialog.Builder dialogBuilder = UIHandler.dialog(pActivity, "", msg);
            dialogBuilder.setPositiveButton(pActivity.getResources().getString(R.string.ok), (dialog, i) -> {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra(com.FingerprintCapture.Utilities.Constants.Fingerprint.sVERIFIEDFINGER_PRINT, true);
                pActivity.setResult(com.FingerprintCapture.Utilities.Constants.Fingerprint.sVerified, intent);
                pActivity.finish();
            });
            dialogBuilder.setCancelable(false);
            dialogBuilder.show();
        }
    }

    /**
     * on IdentificationFailure
     */
    private static AlertDialog.Builder dialogBuilder =null;
    private void onEnrollmentFailResponse(Activity pActivity, String msg) {
        if (pActivity != null && !pActivity.isFinishing()) {
            if(dialogBuilder==null) {
                dialogBuilder = UIHandler.dialog(pActivity, "", msg);
                dialogBuilder.setPositiveButton(pActivity.getResources().getString(R.string.ok), (dialog, i) -> {
                    dialog.dismiss();
                    mBtVerify.setEnabled(false);
                    mBtVerify.setSelected(false);
                    mBtScan.setEnabled(true);
                    mBtScan.setSelected(true);
                    dialogBuilder = null;
                });
                dialogBuilder.show();
            }
        }
    }

    private void playNotifyGif(GifView pImageView, int pID) {

        pImageView.setGifResource(pID);
        pImageView.getGifResource();
        pImageView.play();
    }

    private void stopNotifyGif(GifView pImageView, int pID) {
        mNotifyOverlay.setVisibility(View.GONE);
        pImageView.setGifResource(pID);
        pImageView.getGifResource();
        pImageView.pause();
    }

    public boolean isSignatureVerified() {
        return SignatureVerified;
    }

}
