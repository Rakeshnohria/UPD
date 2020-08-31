package com.FingerprintCapture;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.FingerprintCapture.Services.AllUrl;
import com.FingerprintCapture.Services.RestClient;
import com.FingerprintCapture.Services.UserApi;
import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.Utilities.SharedPrefUtility;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.FingerprintCapture.models.Account;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    private EditText userName, password;
    private Button login;
    private Intent towardsUploadPage;
    private ImageView passwordVisibility;
    private boolean viewPassword = false;
    private RelativeLayout rootLayout;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1000;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    private int customer_id;
    private final String status = "status";
    private final String emailParam = "email";
    private final String passwordParam = "password";
    private LinearLayout progressLayout;
    private RelativeLayout mBackIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);
        FingerprintCaptureApplication.getApplicationInstance().setActivity(this);
        mBackIcon = findViewById(R.id.back_button);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(Constants.sIS_ADD_ACCOUNT)) {
                if (getIntent().getExtras().getBoolean(Constants.sIS_ADD_ACCOUNT)) {
                    mBackIcon.setVisibility(View.VISIBLE);
                    mBackIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }
        }
        if (((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED)) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)) {
            initializeComponents();
        } else {
            checkAndRequestPermissions();
        }
    }


    private void initializeComponents() {
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayoutLoginPage);
//        if (!checkInternetConnection()) {
//            Snackbar snackbar = Snackbar.make(rootLayout, getString(R.string.not_connected_to_network), Snackbar.LENGTH_LONG)
//                    .setAction(R.string.snackbar_action, null);
//            View sbView = snackbar.getView();
//            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
//            snackbar.show();
//        } else {
//            Snackbar snackbar = Snackbar.make(rootLayout, getString(R.string.connected_to_network), Snackbar.LENGTH_LONG)
//                    .setAction(R.string.snackbar_action, null);
//            View sbView = snackbar.getView();
//            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
//            snackbar.show();
//        }
        userName = (EditText) findViewById(R.id.et_user_name);
        password = (EditText) findViewById(R.id.et_password);
        login = (Button) findViewById(R.id.btn_login);
        passwordVisibility = (ImageView) findViewById(R.id.password_visibility);
        userName.requestFocus();
        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                password.setSelection(password.getText().length());
            }
        });
        passwordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPassword == false) {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordVisibility.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.password_view_icon));
                    viewPassword = true;
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordVisibility.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.password_hide_icon));
                    viewPassword = false;
                }
            }
        });
        findViewById(R.id.request_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AllUrl.updRegisterUrl));
                startActivity(browserIntent);
            }
        });
        findViewById(R.id.more_app_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AllUrl.updMoreInfoUrl));
                startActivity(browserIntent);
            }
        });
        findViewById(R.id.send_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getString(R.string.email_updurns_com), null));
                startActivity(emailIntent);
            }
        });
        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + getString(R.string._800_590_4133)));
                startActivity(callIntent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().toString().trim().equals("")) {
                    userName.setError(getString(R.string.non_empty_username));
                } else if (password.getText().toString().trim().equals("")) {
                    password.setError(getString(R.string.non_empty_password));
                } else if (SharedPrefUtility.checkIfAccountAlreadyExistsInPreferences(userName.getText().toString().trim())) {
                    userName.requestFocus();
                    userName.setError(getString(R.string.already_logged_in));
                } else {
                    if (checkInternetConnection()) {
                        /*progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage(getString(R.string.loading));
                        progressDialog.show();*/
                        userName.setEnabled(false);
                        password.setEnabled(false);
                        login.setEnabled(false);
                        callProgress();
                        loginService();
                    } else {
                        Snackbar snackbar = Snackbar.make(rootLayout, getString(R.string.not_connected_to_network), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.snackbar_action), null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                    }

                }
            }
        });
    }

    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int readStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int internetPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        int networkStatePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (internetPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (networkStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                int counter = 0;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        counter++;
                    }
                }
                if (counter == grantResults.length) {
                    initializeComponents();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.permissions_message))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok_message), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    return;
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
        }
    }


    private boolean checkInternetConnection() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().
                    getSystemService(CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    private void loginService() {
        Retrofit retrofit = RestClient.build(AllUrl.baseUrl);
        UserApi userApi = retrofit.create(UserApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put(emailParam, userName.getText().toString().trim());
        params.put(passwordParam, password.getText().toString());
        Call<JsonObject> call = userApi.login(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body().get(status).toString().equals("1")) {
                    sharedPreferences = getSharedPreferences(Constants.SharedPreferences.sKEY_FOR_SHARED_PREFERENCES, MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    customer_id = response.body().get(getString(R.string.shared_preferences_customer_id_key)).getAsInt();
                    editor.putInt(Constants.SharedPreferences.sCUSTOMER_ID, customer_id);
                    editor.putBoolean(Constants.SharedPreferences.sLOGIN_STATUS, true);
                    editor.putString(Constants.SharedPreferences.sEMAIL_ADDRESS, userName.getText().toString());
                    editor.apply();
                    Account account;
//                    if ((SharedPrefUtility.getAccounts() != null) && (SharedPrefUtility.getAccounts().size() > 2)) {
//                        account = new Account(userName.getText().toString().trim()+
//                                "accountAbcdefghijklmnopqrstfjhbfhjsdbfhjsbfhjsfbbbfgkbgmbgkbmkgbmshbfhsj "
//                                +SharedPrefUtility.getAccounts().size(), true,
//                                true, customer_id);
//
//                    } else {
                    account = new Account(userName.getText().toString().trim(), true,
                            true, customer_id);
                   //    }
                    addAccount(account);
                    towardsUploadPage = new Intent(getApplicationContext(), AgentListActivity.class);
                    startActivity(towardsUploadPage);
                    finishAffinity();
                    userName.setEnabled(true);
                    password.setEnabled(true);
                    login.setEnabled(true);
                    progressLayout.setVisibility(View.GONE);

                } else {
                    Snackbar snackbar = Snackbar.make(rootLayout, getString(R.string.invalid_details), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snackbar_action), null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    snackbar.show();
                    progressLayout.setVisibility(View.GONE);
                    userName.setEnabled(true);
                    password.setEnabled(true);
                    login.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(rootLayout, getString(R.string.unable_to_connect), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.snackbar_action), null);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();
                progressLayout.setVisibility(View.GONE);
                userName.setEnabled(true);
                password.setEnabled(true);
                login.setEnabled(true);

            }
        });
    }

    private void callProgress() {
        progressLayout = (LinearLayout) findViewById(R.id.progress_bar_layout);
        progressLayout.setVisibility(View.VISIBLE);
    }

    private void addAccount(Account pAccount) {
        ArrayList<Account> accountsList = SharedPrefUtility.getAccounts();
        if (SharedPrefUtility.getAccounts() == null) {
            accountsList = new ArrayList<>();
        }
        for (int i = 0; i < accountsList.size(); i++) {
            accountsList.get(i).setCurrentAccount(false);
        }
        accountsList.add(pAccount);
        SharedPrefUtility.saveAccounts(accountsList);
    }
}
