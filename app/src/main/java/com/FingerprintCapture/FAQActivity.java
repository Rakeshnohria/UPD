package com.FingerprintCapture;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.application.FingerprintCaptureApplication;

public class FAQActivity extends AppCompatActivity {
    private WebView mFaqContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        initializeComponents();
    }

    private void initializeComponents() {
        mFaqContent = findViewById(R.id.faq_content);
// For Html
//       mFaqContent.
//                loadUrl("file:///android_asset/html/doctor_terms_conditions.html");
        if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
            FingerprintCaptureApplication.getApplicationInstance().showProgress(
                    FAQActivity.this, Constants.sEMPTY_STRING);
        }
        mFaqContent.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                FingerprintCaptureApplication.getApplicationInstance().hideProgress();
            }
        });
        mFaqContent.
                loadUrl(Constants.sFAQ_URL);

    }
}
