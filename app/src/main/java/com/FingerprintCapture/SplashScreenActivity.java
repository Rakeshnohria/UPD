package com.FingerprintCapture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.FingerprintCapture.Utilities.Constants;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends AppCompatActivity {
    private int SPLASH_TIME_OUT = 3000;
    private SharedPreferences sharedPreferences;
    private boolean loginStatus;
    private boolean DEFAULT_VALUE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences(Constants.SharedPreferences.sKEY_FOR_SHARED_PREFERENCES, MODE_PRIVATE);
                loginStatus = sharedPreferences.getBoolean(Constants.SharedPreferences.sLOGIN_STATUS, DEFAULT_VALUE);
                Intent toLoginPage;
                if (loginStatus) {
                    toLoginPage = new Intent(SplashScreenActivity.this, AgentListActivity.class);
                } else {
                    toLoginPage = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }
                startActivity(toLoginPage);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }


}
