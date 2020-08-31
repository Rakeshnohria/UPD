package com.FingerprintCapture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.prechat.ZopimChatActivity;

public class HelpActivity extends AppCompatActivity {
    private Button mStartChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZopimChat.init(Constants.sZANDESK_ACCOUNT_KEY);
        if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
            startChat();
        } else {
            setContentView(R.layout.activity_help);
            initializeComponents();
        }
    }

    private void initializeComponents() {
        mStartChat = findViewById(R.id.start_chat);
        mStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
                    startChat();
                }
            }
        });
    }

    private void startChat() {
        startActivity(new Intent(getApplicationContext(), ZopimChatActivity.class));
        finish();
    }
}
