package com.FingerprintCapture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.FingerprintCapture.BioServerWebServices.BioServerEventHandlers.BioServerIdentifyEventHandler;
import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.FingerprintCapture.models.response.CustomerData;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class VerifyFingerPrint extends AppCompatActivity {


    private ScanFingerPrint mScanFingerPrint;
    private String mFingerPrintId;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1000;
     Button mScanButton, mSaveButton, mVerifyButton;
     ImageView mBackButton,mFingerPrintGif;
     String mFingerprint="";
    private CustomerData mCustomerData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_finger_print);
      //  FingerprintCaptureApplication.getApplicationInstance().setActivity(this);
        initializeViews();
        //get data from previous intent
        mCustomerData=getIntent().getParcelableExtra(Constants.sCUSTOMER_DATA_Verify);
        mScanButton.setSelected(true);

        getSupportActionBar().hide();
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
//after sve and verification




    //initialize view components
    private void initializeViews() {
        mBackButton=findViewById(R.id.action_bar_back_button);
        mScanButton = findViewById(R.id.bt_scan);
        mSaveButton = findViewById(R.id.bt_save);
        mVerifyButton = findViewById(R.id.bt_verify);
        mFingerPrintGif = findViewById(R.id.iv_fingerPrint);
    }

    public void onSave(View pView) {
        mScanFingerPrint.StopScan();
        if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            String base = getFingerPrintBytes();
          //  Log.e("FingerPrintId: ", mFingerPrintId);
            Log.e("image ", base);
            Gson gson = new Gson();
            Log.e("Bitmap: ", gson.toJson(getFingerPrintBytes()));
//            BioServerEnrollmentEventHandler bioServerEnrollmentEventHandler = new BioServerEnrollmentEventHandler(mFingerPrintId, getFingerPrintBytes());
//            bioServerEnrollmentEventHandler.doEnrollment(VerifyFingerPrint.this);
        }
    }

    /**
     * start scanning of Fingerprint
     */
    public void onScan(View pView) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            askPermissions();
        } else {
            scanningFingerprint();
        }
    }

    /**
     * scanning of Fingerprint
     */
    void scanningFingerprint() {
        mScanFingerPrint = new ScanFingerPrint(this, mScanButton,
                mSaveButton, mVerifyButton
                ,mFingerPrintGif);
        mScanFingerPrint.startScan();
        mFingerprint=getFingerPrintBytes();
        Log.d("scanningFingerprint: ", mFingerprint);
    }

    /**
     * onVerify Fingerprint
     */
    public void onVerify(View pView) {
        mScanFingerPrint.StopScan();

        if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
            FingerprintCaptureApplication.getApplicationInstance().showProgress(this, getString(R.string.verifying));
            String fingerPrintId ;
                    /*
                    //send customer id for old customers not having upd id in fingerprint id
                    */
            fingerPrintId=mCustomerData.getUpdId()==null?mCustomerData.getId():mCustomerData.getUpdId();
            BioServerIdentifyEventHandler bioServerIdentifyEventHandler = new BioServerIdentifyEventHandler(fingerPrintId, getFingerPrintBytes(), findViewById(R.id.verify_layout),findViewById(R.id.ly_notifyOverlay));
            bioServerIdentifyEventHandler.doIdentification(VerifyFingerPrint.this,mCustomerData.getId());
        }
        else{
            mScanButton.setEnabled(true);
            mVerifyButton.setEnabled(false);
            mVerifyButton.setSelected(false);
            mScanButton.setSelected(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScanFingerPrint != null) {
            mScanFingerPrint.dispose();
        }
        Thread.currentThread().interrupt();
    }


    private String getFingerPrintBytes() {

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) mFingerPrintGif.getDrawable());
        Bitmap bitmap;
        if (bitmapDrawable == null) {
            mFingerPrintGif.buildDrawingCache();
            bitmap = mFingerPrintGif.getDrawingCache();
            mFingerPrintGif.buildDrawingCache(false);
        } else {
            bitmap = bitmapDrawable.getBitmap();
        }
        return convertToBase64fromBitmap(bitmap);
    }

    /*
    *
    * convert to base 64
     */
    public static String convertToBase64fromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
    private boolean checkAndRequestPermissions() {
        int readStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(VerifyFingerPrint.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    private void askPermissions() {
        /*if (((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED)) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)) {
            SharedPreferencesUtility.setPermissionEnable(AddFingerprint.this, true);
            scanningFingerprint();
        } else {
            checkAndRequestPermissions();
        } */
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED)) {
            scanningFingerprint();
        } else {
            checkAndRequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                int counter = 0;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        counter++;
                    }
                }
                if (counter == grantResults.length) {
                    scanningFingerprint();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.permissions_message))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok_message), (dialog, id) -> {
                                finish();
                                return;
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
        }
    }

}
