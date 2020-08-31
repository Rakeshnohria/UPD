package com.FingerprintCapture.retorfitApiCalls.AgentListing;

import androidx.annotation.NonNull;

import com.FingerprintCapture.R;
import com.FingerprintCapture.Services.AllUrl;
import com.FingerprintCapture.Services.RestClient;
import com.FingerprintCapture.Services.UserApi;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.FingerprintCapture.interfaces.AgentListingListener;
import com.FingerprintCapture.models.request.UserRequest;
import com.FingerprintCapture.models.response.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AgentListingCall {
    private UserApi mApiService;
    private AgentListingListener mAgentListingListener;

    public AgentListingCall(AgentListingListener pAgentListingListener) {
        mAgentListingListener = pAgentListingListener;
    }


    public void getAgentListing(UserRequest userRequest, boolean showProgress, final boolean isSearchApiCall) {
        if (showProgress) {
            mAgentListingListener.showProgress();
        }
        Retrofit retrofit = RestClient.build(AllUrl.baseUrl);
        mApiService = retrofit.create(UserApi.class);
        Call<UserResponse> userResponseCall = mApiService.doUserRequest(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                mAgentListingListener.hideProgress();
                UserResponse results = response.body();
                if (results != null) {
                    mAgentListingListener.onSuccess(results, isSearchApiCall);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                mAgentListingListener.hideProgress();
                mAgentListingListener.onError(FingerprintCaptureApplication.getApplicationInstance()
                        .getString(R.string.unable_to_connect));
            }
        });
    }
}
