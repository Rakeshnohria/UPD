package com.FingerprintCapture.interfaces;

public interface AgentListingListener {
    void onSuccess(Object object, boolean isSearchApiCall);

    void onError(String message);

    void showProgress();

    void hideProgress();
}
