package com.FingerprintCapture.application;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.FingerprintCapture.R;
import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.models.response.CustomerData;
import com.google.android.material.snackbar.Snackbar;


public class FingerprintCaptureApplication extends Application {
    private ProgressDialog mProgressDialog;
    AppCompatActivity activity;
    static FingerprintCaptureApplication mApplication;
    Dialog mDialog;
    CustomerData mCustomerData;
    boolean isUpdated;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public CustomerData getmCustomerData() {
        return mCustomerData;
    }

    public void setmCustomerData(CustomerData mCustomerData) {
        this.mCustomerData = mCustomerData;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public static FingerprintCaptureApplication getApplicationInstance() {
        return mApplication;
    }

    public AppCompatActivity getActivity() {
        return this.activity;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void showProgress(Context pContext, String pMessage) {
        if (pContext != null) {
            Activity activity = (Activity) pContext;
            if (activity.isFinishing())
                return;
            /*
             * If the progress bar is already showing need not create a new one
             */
            if (this.mProgressDialog == null || !this.mProgressDialog.isShowing()) {
                mProgressDialog = ProgressDialog.show(pContext, null, null);

                /*
                 *  To show progress bar with and without message
                 */
                //
                ProgressBar spinner = new android.widget.ProgressBar(pContext, null, android.R.attr.progressBarStyle);
                spinner.getIndeterminateDrawable().setColorFilter(this.getResources().getColor(R.color.progress), android.graphics.PorterDuff.Mode.MULTIPLY);
                LinearLayout progress = new LinearLayout(this);
                progress.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                progress.setOrientation(LinearLayout.VERTICAL);
                progress.addView(spinner);
                if (!(pMessage.trim().equals(Constants.sEMPTY_STRING))) {
                    TextView message=new TextView(this);
                    message.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    message.setTextColor(this.getResources().getColor(R.color.progress));
                    message.setGravity(Gravity.CENTER);
                    message.setText(pMessage);
                    message.setTextSize(16f);
                    progress.addView(message);

                }


                if (mProgressDialog.getWindow() != null) {
                    mProgressDialog.getWindow().setBackgroundDrawable
                            (new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }
                mProgressDialog.setContentView(progress);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }
    }

    public void hideProgress() {
        if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public Boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
            if (!isConnected) {
                if (getActivity() != null) {
                    showToast(getString(R.string.not_connected_to_network));
                    //  showSnackBar(getActivity().getCurrentFocus(), getString(R.string.not_connected_to_network));
                }
            }

        }
        return isConnected;
    }

    public void showToast(String pString) {
        Toast.makeText(this, pString, Toast.LENGTH_SHORT).show();
    }

    public void showSnackBar(View pView, String message) {
        if (pView == null) {
            showToast(getString(R.string.not_connected_to_network));
            return;
        }

        Snackbar snackbar = Snackbar.make(pView, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action, null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        snackbar.show();
    }

    public Dialog showCustomDialog(Context pContext, int layout) {
        if (mDialog == null) {
            mDialog = new Dialog(pContext);
            if (mDialog.getWindow() != null) {
                mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(pContext,
                        R.color.darkTransparentColor)));
            }
            mDialog.setCancelable(false);
            View view = getActivity().getLayoutInflater().inflate(layout, null);
            mDialog.setContentView(view);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        return mDialog;
    }

    public void dismissCustomDialog() {
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
            this.mDialog = null;
        }
    }

}
