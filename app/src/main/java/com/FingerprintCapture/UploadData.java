package com.FingerprintCapture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.FingerprintCapture.BioServerWebServices.BioServerServices.EnrollSuccess;
import com.FingerprintCapture.Services.AllUrl;
import com.FingerprintCapture.Services.RestClient;
import com.FingerprintCapture.Services.UserApi;
import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.Utilities.SharedPrefUtility;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.FingerprintCapture.models.response.CustomerData;
import com.FingerprintCapture.models.response.UploadImageResponse;
import com.bumptech.glide.Glide;
import com.futronictech.MyBitmapFile;
import com.futronictech.Scanner;
import com.futronictech.SelectFileFormatActivity;
import com.futronictech.UsbDeviceDataExchangeImpl;
import com.futronictech.ftrWsqAndroidHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.FingerprintCapture.Utilities.Constants.sCUSTOMER_DATA;


public class UploadData extends Activity implements EnrollSuccess {
    // Data for scan
    private static Button mButtonScan;
    //    private static TextView mScannerInfo;
    // public static boolean Constants.FPScanConstants.mStop = false;
    // public static boolean Constants.FPScanConstants.mFrame = true;
    // public static boolean Constants.FPScanConstants.mLFD = false;
    //public static boolean Constants.FPScanConstants.mInvertImage = false;

    public static final int MESSAGE_SHOW_MSG = 1;
    public static final int MESSAGE_SHOW_SCANNER_INFO = 2;
    public static final int MESSAGE_SHOW_IMAGE = 3;
    public static final int MESSAGE_ERROR = 4;
    public static final int MESSAGE_TRACE = 5;

    // public static byte[] Constants.FPScanConstants.mImageFP = null;
    //public static Object Constants.FPScanConstants.mSyncObj = new Object();

    //public static int Constants.FPScanConstants.mImageWidth = 0;
    //public static int Constants.FPScanConstants.mImageHeight = 0;
    // private static int[] Constants.FPScanConstants.mPixels = null;
    // private static Bitmap Constants.FPScanConstants.mBitmapFP = null;
    //private static Canvas Constants.FPScanConstants.mCanvas = null;
    //private static Paint Constants.FPScanConstants.mPaint = null;
    TextView textView123;
    private FPScan mFPScan = null;
    //
    //public static boolean Constants.FPScanConstants.mUsbHostMode = true;
    final Handler handlerForCountDown = new Handler();
    java.util.concurrent.atomic.AtomicInteger n = new AtomicInteger(3);
    // Intent request codes
    private static final int REQUEST_FILE_FORMAT = 1;
    private UsbDeviceDataExchangeImpl usb_host_ctx = null;
    //End for scan data

    private EditText name, firstName, email, id;
    private TextView date;
    private Button upload;
    private static ImageView userImage;
    private Uri mCapturedImageURI;
    private DatePickerDialog datePickerDialog;
    private final int REQUEST_CAMERA = 101;
    public static final int sGALLERY_DATA_CODE = 200;
    /* private final int REQUEST_IMAGE_PICK=102;*/
    private boolean doubleBackToExitPressedOnce = false;
    private String temporaryFileName = "temp.jpg";
    private int day, year, month;
    private boolean isDefaultImage = true;
    private View rootView;

    private Intent toLoginPage;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor;
    private String customer_id;
    private File imgFile;
    private String realPath;
    private final String status = "status";
    private Uri myUri, tempUri;
    int progressStatus = 0;
    private Handler handler = new Handler();
    //  private TextView tvProgress;
    private ProgressBar mProgress;
    private LinearLayout progressLayout;
    private final int minProgress = 0, maxProgress = 99, THREAD_SLEEP_TIME = 200;
    private File photoFile;
    private Uri imageUri, thumbNailUri;
    private String mBitmapImage = "";
    private Boolean mIsImage = true, mUploadPrint;
    private String imagePath;
    private File imageFile, thumbNailfile;
    private int THUMBNAIL_SIZE = 100;
    private byte[] arr;
    private TextView tvProgress;
    private CustomerData mCustomerData = null;
    private String mItemId = Constants.sEMPTY_STRING;
    private boolean isUpdateDataCall = false;
    private Button mPrintButton, mEditButton, mVerifyAddButon;
    private LinearLayout mCameraScanLinearLayout, mOptionEditPrintLinearLayout, mVerifyButtonContainer;
    ImageButton mBackButton;
    Button mCamerButton;
    boolean isProcessing = false;
    boolean mIsSubscribed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_data);
        initializeComponents();
        if (getIntent() != null && getIntent().getParcelableExtra(sCUSTOMER_DATA) != null) {
            mIsSubscribed = getIntent().getBooleanExtra(Constants.sIsSubscribed, false);

            mCustomerData = getIntent().getParcelableExtra(sCUSTOMER_DATA);
            mUploadPrint = mCustomerData.getFingerPrint() == 0;
            setVerifyButtonVisibilty(mIsSubscribed);
            setDataToViewGettingFromIntent(mCustomerData);
            isUpdateDataCall = true;
            name.setEnabled(false);
            date.setEnabled(false);
            firstName.setEnabled(false);
            email.setEnabled(false);
            id.setEnabled(false);
            mCameraScanLinearLayout.setVisibility(View.GONE);
            mOptionEditPrintLinearLayout.setVisibility(View.VISIBLE);
            /*
            //if agent is subscibed
            if(){
               mVerifyButtonContainer.setVisibility(View.GONE);
            }
             */

        } else {
            name.requestFocus();
            mUploadPrint = true;
            mCameraScanLinearLayout.setVisibility(View.VISIBLE);
            mOptionEditPrintLinearLayout.setVisibility(View.GONE);
            mVerifyButtonContainer.setVisibility(View.GONE);
        }

//        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        //isDefaultImage = false;
//        imageUri = Uri.fromFile(photoFile);
//        bitmapOfImage(imageUri);

        Constants.FPScanConstants.mFrame = true;
        Constants.FPScanConstants.mUsbHostMode = true;
        Constants.FPScanConstants.mLFD = Constants.FPScanConstants.mInvertImage = false;
        mButtonScan = (Button) findViewById(R.id.btn_scan);
//        mButtonStop = (Button) findViewById(com.futronictech.R.id.upload);
//        mScannerInfo = (TextView) findViewById(R.id.tvScannerInfo);
        userImage = (ImageView) findViewById(R.id.user_image);
        userImage.setImageResource(R.drawable.image_upload_icon);
        textView123 = (TextView) findViewById(com.futronictech.R.id.textView123);
        usb_host_ctx = new UsbDeviceDataExchangeImpl(this, mHandler);
        mButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsImage = false;
                startScan();
            }
        });


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        mVerifyAddButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCustomerData.getFingerPrint() == 1) {
                    verify(true);
                } else {
                    openAddImageLayout();
                }
            }
        });
//        findViewById(com.futronictech.R.id.upload).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Constants.FPScanConstants.mStop = true;
//                if (mFPScan != null) {
//                    mFPScan.stop();
//                    mFPScan = null;
//
//                }
//                mButtonScan.setEnabled(true);
//                uploadImage();
//            }
//        });
    }

    private void verify(Boolean pVerify) {
        Intent intent = new Intent(this, VerifyFingerPrint.class);
        if (pVerify) {
            intent.putExtra(Constants.sCUSTOMER_DATA_Verify, mCustomerData);
        }
        startActivityForResult(intent, Constants.Fingerprint.sVerify);
    }

    /*
     ** set add/verify button visibility
     */
    private void setVerifyButton(Boolean pIsVerified) {
        if (pIsVerified) {
            //if finger print available and verified disable button
            mVerifyAddButon.setText(R.string.already_verified);
            mVerifyAddButon.setBackgroundColor(getResources().getColor(R.color.buttonGreen));
            mVerifyAddButon.setEnabled(false);

        } else {
            //if fingerPrint avialable then verify
            mVerifyAddButon.setText(R.string.verify_fingerPrint);
        }
    }

    private void setVerifyButtonVisibilty(boolean pVisibility) {
//        if (pVisibility) {
//             mVerifyButtonContainer.setVisibility(View.VISIBLE);
//            if (mCustomerData.getFingerPrint() == 0) {
//                mVerifyAddButon.setText(getString(R.string.add_to_verify));
//            } else {
//                setVerifyButton(mCustomerData.getVerifiedUser() == 1);
//            }
//        } else {
//            mVerifyButtonContainer.setVisibility(View.GONE);
//        }
    }

    private void changeEditButtonToSaveButton() {
        mEditButton.setText(R.string.save);
        int buttonWidth = getScreenWidth();
        mPrintButton.setVisibility(View.GONE);
        mEditButton.setWidth(buttonWidth);
    }

    @Override
    public void onBackPressed() {
        if (!isProcessing) {
            setData();
            super.onBackPressed();
        }
    }

    private void setData() {
        Intent intent = new Intent();
        intent.putExtra(Constants.sCUSTOMER_DATA, mCustomerData);
        setResult(Constants.RequestCode.sGALLERY_DATA_CODE, intent);
        FingerprintCaptureApplication.getApplicationInstance().setmCustomerData(mCustomerData);
        finish();
    }

    private void setDataToViewGettingFromIntent(CustomerData pCustomerData) {
        if (pCustomerData != null && pCustomerData.getName() != null) {
            name.setText(String.valueOf(pCustomerData.getName()));
            if (pCustomerData.getEmail() != null) {
                email.setText(pCustomerData.getEmail());
            }
            if (pCustomerData.getFirstName() != null) {
                firstName.setText(pCustomerData.getFirstName());
            }
            if (pCustomerData.getId() != null && !pCustomerData.getAgentId().trim().isEmpty()) {
                id.setText(pCustomerData.getAgentId());
                id.setEnabled(false);
            }
            if (pCustomerData.getDob() != null && !pCustomerData.getDob().trim().isEmpty()) {
                String dateText = pCustomerData.getDob();
                if (dateText.toLowerCase().contains("dd")) {
                    dateText = Constants.sEMPTY_STRING;
                }
                date.setText(dateText);
                date.setEnabled(false);
            }

            mItemId = pCustomerData.getId();
        }
    }


    private void initializeComponents() {
        customer_id = Constants.sEMPTY_STRING + SharedPrefUtility.getCustomerId(this);
        //Toast.makeText(getApplicationContext(),sharedPreferences.getInt(CUSTOMER_ID_SP,0)+"",Toast.LENGTH_SHORT).show();
        rootView = findViewById(R.id.root_layout);
        name = findViewById(R.id.et_user_name);
        firstName = findViewById(R.id.et_first_name);
        email = findViewById(R.id.et_email);
        id = findViewById(R.id.et_id);
        date = findViewById(R.id.et_date);
        upload = findViewById(R.id.btn_upload_data);
        mPrintButton = findViewById(R.id.btn_print);
        mEditButton = findViewById(R.id.btn_edit);
        mCameraScanLinearLayout = findViewById(R.id.camera_scan_linear_layout);
        mOptionEditPrintLinearLayout = findViewById(R.id.button_options_edit_print);
        mBackButton = findViewById(R.id.action_bar_back_button);
        mCamerButton = findViewById(R.id.btn_camera);
        mVerifyAddButon = findViewById(R.id.verify_btn);
        mVerifyButtonContainer = findViewById(R.id.verify_btn_container);
//        userImage = (ImageView) findViewById(R.id.user_image);
//        userImage.setImageResource(R.drawable.image_upload_icon);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(UploadData.this, dateSetListener, year,
                        month,
                        day);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());

            }
        });
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        String dateToShow = new SimpleDateFormat("dd/MM/yyyy").format(time);
        //  date.setText(dateToShow);
        mCamerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsImage = true;
                mBitmapImage = "";
                Constants.FPScanConstants.mStop = true;
                if (mFPScan != null) {
                    mFPScan.stop();
                    mFPScan = null;
                }
                mButtonScan.setEnabled(true);
                EasyImage.configuration(UploadData.this)
                        .setImagesFolderName("EasyImage sample");
                EasyImage.openCamera(UploadData.this, REQUEST_CAMERA);
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditClick();
            }
        });
        mPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddImageLayout();
            }
        });
//        userImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EasyImage.configuration(UploadData.this)
//                        .setImagesFolderName("EasyImage sample");
//                EasyImage.openCamera(UploadData.this, REQUEST_CAMERA);
//            }
//                              /*  } *//*else if (items[item].equals(getString(R.string.choose_from_gallery))) {
//                                    Intent intent = new Intent(Intent.ACTION_PICK,
//                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                    startActivityForResult(intent, REQUEST_IMAGE_PICK);
//                                } *//*else if (items[item].equals(getString(R.string.cancel))) {
//                                    dialog.dismiss();
//                                }
//                            }
//                        });
//                        builder.show();*/
//            //}
//        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().equals("")) {
                    name.setError(getString(R.string.non_empty_name_of_deceased));
                }
//                else if (date.getText().toString().trim().equals("")) {
//                    date.setError(getString(R.string.non_empty_date));
//                }
                else if (email.getText().length() > 0 && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    email.setError(getString(R.string.incorrect_email));
                } else if (isDefaultImage) {
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.please_select_an_image), Snackbar.LENGTH_LONG)
                            .setAction(R.string.snackbar_action, null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    snackbar.show();

                } else {
                    Constants.FPScanConstants.mStop = true;
                    if (mFPScan != null) {
                        mFPScan.stop();
                        mFPScan = null;

                    }
                    mButtonScan.setEnabled(true);
                    if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
                        call();

                    } else {
                        Snackbar snackbar = Snackbar.make(view, getString(R.string.not_connected_to_network), Snackbar.LENGTH_LONG)
                                .setAction(R.string.snackbar_action, null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            }
        });
    }

    private void call() {
        userImage.setClickable(false);
//                        date.setEnabled(false);
        name.setEnabled(false);
        id.setEnabled(false);
        date.setEnabled(false);
        mCamerButton.setEnabled(false);
        firstName.setEnabled(false);
        email.setEnabled(false);
        upload.setEnabled(false);
        mButtonScan.setEnabled(false);
//        callProgress();
        uploadService();
    }

    private boolean validationForUpdate() {
        if (name.getText().toString().trim().equals("")) {
            name.setError(getString(R.string.non_empty_name_of_deceased));
            return false;
        }
//        else if (date.getText().toString().trim().equals("")) {
//            date.setError(getString(R.string.non_empty_date));
//            return false;
//        }
        else if (email.getText().length() > 0 && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            email.setError(getString(R.string.incorrect_email));
            return false;
        }
        return true;
    }

    private void openGalleryActivity(CustomerData customerData) {
        Intent intent = new Intent(this, AgentGalleryActivity.class);
        intent.putExtra(sCUSTOMER_DATA, customerData);
        startActivityForResult(intent, Constants.RequestCode.sGALLERY_DATA_CODE);
    }

    private void onEditClick() {
        if (mEditButton.getText().equals(getString(R.string.save))) {
            boolean validate = validationForUpdate();
            if (validate) {
                if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
                    name.setEnabled(false);
                    id.setEnabled(false);
                    date.setEnabled(false);
                    firstName.setEnabled(false);
                    email.setEnabled(false);
                    mEditButton.setEnabled(false);
                    mBackButton.setEnabled(false);

                    uploadService();
                    FingerprintCaptureApplication.getApplicationInstance().showProgress(this, null);
                } else {
                    FingerprintCaptureApplication.getApplicationInstance().showSnackBar(rootView,
                            getString(R.string.not_connected_to_network));
                }
            }
        } else if (mEditButton.getText().equals(getString(R.string.edit))) {
            changeEditButtonToSaveButton();
            firstName.setEnabled(true);
            email.setEnabled(true);
            name.setEnabled(true);
            name.requestFocus();
            name.setSelection(name.getText().length());
            id.setEnabled(true);
            date.setEnabled(true);
        }
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width;
    }

    public void openAddImageLayout() {
        Intent intent = new Intent(this, AddImageActivity.class);
        intent.putExtra(sCUSTOMER_DATA, mCustomerData);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_FILE_FORMAT:
//                if (resultCode == Activity.RESULT_OK) {
//                    // Get the file format
//                    String[] extraString = data.getExtras().getStringArray(SelectFileFormatActivity.EXTRA_FILE_FORMAT);
//                    String fileFormat = extraString[0];
//                    String fileName = extraString[1];
//                    SaveImageByFileFormat(fileFormat, fileName);
//                } else
//                    showToast("Cancelled!");
//                break;
//            case REQUEST_CAMERA:
        //result after verification
        if (requestCode == Constants.Fingerprint.sVerify && data != null && resultCode == Constants.Fingerprint.sVerified) {
            if (data.getBooleanExtra(com.FingerprintCapture.Utilities.Constants.Fingerprint.sVERIFIEDFINGER_PRINT, false)) {
                mCustomerData.setVerifiedUser(1);
                setVerifyButtonVisibilty(mIsSubscribed);
            }

        }
        //result after add fingerprint
        else if (resultCode == Constants.RequestCode.sGALLERY_DATA_CODE && data != null) {
            CustomerData customerData = data.getParcelableExtra(sCUSTOMER_DATA);
            if (customerData != null) {
                mCustomerData = customerData;
                setVerifyButtonVisibilty(mIsSubscribed);
            }
        }


        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    photoFile = EasyImage.lastlyTakenButCanceledPhoto(getApplicationContext());
                    Glide.with(UploadData.this).load(Uri.fromFile(photoFile)).into(userImage);
                    isDefaultImage = false;
                    imageUri = Uri.fromFile(photoFile);
                    bitmapOfImage(imageUri);
                }
            }
        });
//        }

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                isDefaultImage = false;
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri uri = data.getData();
                myUri = uri;
                tempUri = getImageUri(getApplicationContext(), photo);
                System.out.println(uri + "    uri uri " + tempUri);
                imgFile = new File(getRealPathFromURI(tempUri));
                mCapturedImageURI = tempUri;
                realPath = getRealPathFromUri(getApplicationContext(), tempUri);
                System.out.println(realPath + "  hell of it" + mCapturedImageURI);
                Glide.with(UploadData.this).load(Uri.fromFile(imgFile)).into(userImage);
            }
        }
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            date.setText(day + "/" + (month + 1) + "/" + year);
            date.setError(null);
        }
    };


    @NonNull
    private RequestBody createPartFromString(String string) {
        return RequestBody.create(MultipartBody.FORM, string);
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        if (fileUri != null) {
            File file = new File(fileUri.getPath().toString());
            imageFile = file;
            System.out.println("getfile " + file.toString() + " " + file.getName());
            RequestBody requestBody = RequestBody.create(MediaType.parse(fileUri.getPath().toString()), file);
            return MultipartBody.Part.createFormData(partName, fileUri.getPath(), requestBody);
        }
        return null;
    }


    private void uploadService() {
        System.out.println("mcaptured image uri" + mCapturedImageURI + "hello upload");
        FingerprintCaptureApplication.getApplicationInstance().showProgress(this, getString(R.string.saving_data));
        if (Constants.FPScanConstants.mBitmapFP != null && !mIsImage) {
            mBitmapImage = VerifyFingerPrint.convertToBase64fromBitmap(Constants.FPScanConstants.mBitmapFP);
        }
        Retrofit retrofit = RestClient.build(AllUrl.baseUrl);
        UserApi userApi = retrofit.create(UserApi.class);
        isProcessing = true;
        // Toast.makeText(this,mBitmapImage,Toast.LENGTH_LONG).show();

        FingerprintCaptureApplication.getApplicationInstance().setUpdated(true);
        Call<JsonObject> call = userApi.uploadData(createPartFromString(customer_id),
                createPartFromString(name.getText().toString()),
                createPartFromString(date.getText().toString()),
                createPartFromString(id.getText().toString()),
                createPartFromString(mItemId),
                createPartFromString(firstName.getText().toString()),
                createPartFromString(email.getText().toString()),
                createPartFromString(mBitmapImage),
                prepareFilePart("image", imageUri),
                prepareFilePart("thumb_image", thumbNailUri)

        );


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {
                if (response.body().get(status).toString().equals("200")) {
                    final UploadImageResponse uploadImageResponse = new Gson().fromJson(response.body(), UploadImageResponse.class);
//                    progressDialog.dismiss();
//                    CustomerData customerData = uploadImageResponse.getCustomerData().get(0);
//                    String fingerPrintId ;
                    /*
                    //send customer id for old customers not having upd id
                    */
//                    fingerPrintId=customerData.getUpdId()==null?customerData.getId():customerData.getUpdId();
//                    if ( !mIsImage ) {
//                        if (progressLayout != null) {
//                            progressLayout.setVisibility(View.GONE);
//                        }
//                        BioServerEnrollmentEventHandler bioServerEnrollmentEventHandler = new BioServerEnrollmentEventHandler(fingerPrintId, mBitmapImage, UploadData.this);
//                        bioServerEnrollmentEventHandler.doEnrollment(UploadData.this, uploadImageResponse);
//                    } else {
//                        success(uploadImageResponse);
//                    }
                    success(uploadImageResponse);

                } else {
//                    FingerprintCaptureApplication.getApplicationInstance().showSnackBar(rootView, getString(R.string.failed_to_upload));
                    fail(response.body().get(status).toString());
//                    Toast.makeText(UploadData.this,"OnFail"+response.body().get(status).toString(),Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                fail(t.getMessage());
//                Toast.makeText(UploadData.this,"OnFailure"+t,Toast.LENGTH_LONG).show();
            }

        });


    }

    @Override
    public void success(UploadImageResponse uploadImageResponse) {
        //progressDialog.dismiss();
        isProcessing = false;
        if (progressLayout != null) {
            progressLayout.setVisibility(View.GONE);
        }
        FingerprintCaptureApplication.getApplicationInstance().hideProgress();


/*
        FingerprintCaptureApplication.getApplicationInstance().showSnackBar(rootView, getString(R.string.upload_successful));
*/
        date.setError(null);
        String message, title = "";
        message = uploadImageResponse.getCustomerData().get(0).getUpdId();
        title = getString(R.string.upd_id);

        if (getIntent() != null && getIntent().getParcelableExtra(sCUSTOMER_DATA) != null) {
            title = "";
            message = getString(R.string.updated_successfully);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadData.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton(getString(R.string.ok_message), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.sCUSTOMER_DATA_ARRAY, uploadImageResponse);
                        if (isUpdateDataCall) {
                            setResult(Constants.RequestCode.sCUSTOMER_DATA_CODE, intent);
                        } else {
                            setResult(Constants.RequestCode.sCUSTOMER_DATA_NEW, intent);
                        }
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        if (!isFinishing()) {
            alert.show();

            name.setEnabled(true);
            firstName.setEnabled(true);
            date.setEnabled(true);
            email.setEnabled(true);
            userImage.setImageResource(R.drawable.image_upload_icon);
            isDefaultImage = true;
            userImage.setClickable(true);
//                        date.setEnabled(true);
            upload.setEnabled(true);
        }


    }

    /*
    fail() called on failure result from local or biosever
    also called when on response status !=200
     */
    @Override
    public void fail(String message) {
        FingerprintCaptureApplication.getApplicationInstance().hideProgress();
        FingerprintCaptureApplication.getApplicationInstance().showSnackBar(rootView, getString(R.string.failed_to_upload));
        if (progressLayout != null) {
            progressLayout.setVisibility(View.GONE);
        }
        userImage.setClickable(true);
//                            date.setEnabled(true);
        mCamerButton.setEnabled(true);
        mButtonScan.setEnabled(true);
        name.setEnabled(true);
        upload.setEnabled(true);
        isProcessing = false;
        firstName.setEnabled(true);
        email.setEnabled(true);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UploadData.this);
        alertDialog.setMessage(getString(R.string.failed_to_upload));
        alertDialog.setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                call();

            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (getIntent() == null) {
                    dialog.dismiss();
                }
//                            date.setText("");
                // userImage.setImageResource(R.drawable.image_upload_icon);
                // isDefaultImage = true;

            }
        });
        alertDialog.show();
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 1, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
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

    private void createthumbnail() {
        String fileName = "MyFile";
        FileOutputStream outputStream = null;
        try {
            File file = new File(getCacheDir(), fileName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            int bytes = bitmap.getByteCount();
            ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
            bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
            byte[] array = buffer.array(); //Get the underlying array containing the data.
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(array);
            outputStream.close();
            thumbNailUri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bitmapOfImage(Uri uri) {

        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(uri.getPath()),
                THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        thumbNailUri = Uri.fromFile(new File(saveToInternalStorage(thumbImage)));
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File path = Environment.getExternalStorageDirectory();
        File mypath = new File(path.getAbsolutePath(), "image.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    private String saveFingerprintToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File path = Environment.getExternalStorageDirectory();
        File mypath = new File(path.getAbsolutePath(), "fingerprint.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }


    // For scanning
//    public static void InitFingerPictureParameters(int wight, int height) {
//        Constants.FPScanConstants.mImageWidth = wight;
//        Constants.FPScanConstants.mImageHeight = height;
//
//        Constants.FPScanConstants.mImageFP = new byte[Constants.FPScanConstants.mImageWidth * Constants.FPScanConstants.mImageHeight];
//        Constants.FPScanConstants.mPixels = new int[Constants.FPScanConstants.mImageWidth * Constants.FPScanConstants.mImageHeight];
//
//        Constants.FPScanConstants.mBitmapFP = Bitmap.createBitmap(wight, height, Bitmap.Config.RGB_565);
//
//        Constants.FPScanConstants.mCanvas = new Canvas(Constants.FPScanConstants.mBitmapFP);
//        Constants.FPScanConstants.mPaint = new Paint();
//
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);
//        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
//        Constants.FPScanConstants.mPaint.setColorFilter(f);
//    }

    private void showCountDown() {
        n = new AtomicInteger(3);
        textView123.setVisibility(View.VISIBLE);
        Runnable counter = new Runnable() {
            @Override
            public void run() {
                textView123.setText(Integer.toString(n.get()));
                if (n.getAndDecrement() >= 1)
                    handlerForCountDown.postDelayed(this, 1000);
                else {
                    textView123.setVisibility(View.GONE);
                    if (StartScan()) {
                        mButtonScan.setEnabled(false);
//                        mButtonStop.setEnabled(true);
                    }
                }
            }
        };
        handlerForCountDown.postDelayed(counter, 1000);
    }

    private void startScan() {
        Constants.FPScanConstants.InitFingerPictureParameters(userImage.getWidth(), userImage.getHeight());
        if (mFPScan != null) {
            Constants.FPScanConstants.mStop = true;
            mFPScan.stop();

        }
        Constants.FPScanConstants.mStop = false;
        if (Constants.FPScanConstants.mUsbHostMode) {
            usb_host_ctx.CloseDevice();
            if (usb_host_ctx.OpenDevice(0, true)) {
                if (StartScan()) {
                    mButtonScan.setEnabled(false);
//                    mButtonStop.setEnabled(true);
                }
//                showCountDown();
            } else {
                if (!usb_host_ctx.IsPendingOpen()) {
                    showToast(getString(R.string.device_error));
                }
            }
        } else {
            if (StartScan()) {
                mButtonScan.setEnabled(false);
//                mButtonStop.setEnabled(true);
            }
        }
    }

//    private void uploadImage() {
//        Bitmap bm = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
//        finalScan.setImageBitmap(bm);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.FPScanConstants.mStop = true;
        if (mFPScan != null) {
            mFPScan.stop();
            mFPScan = null;
        }
        usb_host_ctx.CloseDevice();
        usb_host_ctx.Destroy();
        usb_host_ctx = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private boolean StartScan() {
        mFPScan = new FPScan(usb_host_ctx, mHandler);
        mFPScan.start();
        return true;
    }

    private void SaveImage() {
        Intent serverIntent = new Intent(this, SelectFileFormatActivity.class);
        startActivityForResult(serverIntent, REQUEST_FILE_FORMAT);
    }

//    private void SaveImageByFileFormat(String fileFormat, String fileName) {
//        if (fileFormat.compareTo("WSQ") == 0)    //save wsq file
//        {
//            Scanner devScan = new Scanner();
//            boolean bRet;
//            if (Constants.FPScanConstants.mUsbHostMode)
//                bRet = devScan.OpenDeviceOnInterfaceUsbHost(usb_host_ctx);
//            else
//                bRet = devScan.OpenDevice();
//            if (!bRet) {
//                showToast(devScan.GetErrorMessage());
//                return;
//            }
//            byte[] wsqImg = new byte[Constants.FPScanConstants.mImageWidth * Constants.FPScanConstants.mImageHeight];
//            long hDevice = devScan.GetDeviceHandle();
//            ftrWsqAndroidHelper wsqHelper = new ftrWsqAndroidHelper();
//            if (wsqHelper.ConvertRawToWsq(hDevice, Constants.FPScanConstants.mImageWidth, Constants.FPScanConstants.mImageHeight, 2.25f, Constants.FPScanConstants.mImageFP, wsqImg)) {
//                File file = new File(fileName);
//                try {
//                    FileOutputStream out = new FileOutputStream(file);
//                    out.write(wsqImg, 0, wsqHelper.mWSQ_size);    // save the wsq_size bytes data to file
//                    out.close();
//                    showToast("Image is saved as " + fileName);
//                } catch (Exception e) {
//                    showToast("Exception in saving file");
//                }
//            } else
//                showToast("Failed to convert the image!");
//            if (Constants.FPScanConstants.mUsbHostMode)
//                devScan.CloseDeviceUsbHost();
//            else
//                devScan.CloseDevice();
//            return;
//        }
//        // 0 - save bitmap file
//        File file = new File(fileName);
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            //Constants.FPScanConstants.mBitmapFP.compress(Bitmap.CompressFormat.PNG, 90, out);
//            MyBitmapFile fileBMP = new MyBitmapFile(Constants.FPScanConstants.mImageWidth, Constants.FPScanConstants.mImageHeight, Constants.FPScanConstants.mImageFP);
//            out.write(fileBMP.toBytes());
//            out.close();
//            showToast("Image is saved as " + fileName);
//        } catch (Exception e) {
//            showToast("Exception in saving file");
//        }
//    }

    // The Handler that gets information back from the FPScanNewN
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_MSG:
                    String showMsg = (String) msg.obj;
//                    showToast(showMsg);
                    break;
                case MESSAGE_SHOW_SCANNER_INFO:
                    String showInfo = (String) msg.obj;
//                    mScannerInfo.setText(showInfo);
                    break;
                case MESSAGE_SHOW_IMAGE:
                    ShowBitmap();
                    break;
                case MESSAGE_ERROR:
                    //mFPScan = null;
                    mButtonScan.setEnabled(true);
                    mFPScan.stop();
//                  mButtonStop.setEnabled(false);
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_ALLOW_DEVICE:
                    if (usb_host_ctx.ValidateContext()) {
                        if (StartScan()) {
                            mButtonScan.setEnabled(false);
//                          mButtonStop.setEnabled(true);
                        }
                    } else {
                        FingerprintCaptureApplication.getApplicationInstance().showToast("Can't open scanner device");
                    }
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_DENY_DEVICE:
                    FingerprintCaptureApplication.getApplicationInstance().showToast("User deny scanner device");
                    break;
            }
        }
    };

    private void ShowBitmap() {
        for (int i = 0; i < Constants.FPScanConstants.mImageWidth * Constants.FPScanConstants.mImageHeight; i++) {
            Constants.FPScanConstants.mPixels[i] = Color.rgb(Constants.FPScanConstants.mImageFP[i], Constants.FPScanConstants.mImageFP[i], Constants.FPScanConstants.mImageFP[i]);
        }
        Constants.FPScanConstants.mCanvas.drawBitmap(Constants.FPScanConstants.mPixels, 0, Constants.FPScanConstants.mImageWidth, 0, 0, Constants.FPScanConstants.mImageWidth, Constants.FPScanConstants.mImageHeight, false, Constants.FPScanConstants.mPaint);
        userImage.setImageBitmap(Constants.FPScanConstants.mBitmapFP);
        userImage.invalidate();
        imageUri = Uri.fromFile(new File(saveFingerprintToInternalStorage(Constants.FPScanConstants.mBitmapFP)));
        isDefaultImage = false;
        bitmapOfImage(imageUri);
        synchronized (Constants.FPScanConstants.mSyncObj) {
            Constants.FPScanConstants.mSyncObj.notifyAll();
        }
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(this, "On Result", Toast.LENGTH_SHORT).show();
//        switch (requestCode) {
//            case REQUEST_FILE_FORMAT:
//                if (resultCode == Activity.RESULT_OK) {
//                    // Get the file format
//                    String[] extraString = data.getExtras().getStringArray(SelectFileFormatActivity.EXTRA_FILE_FORMAT);
//                    String fileFormat = extraString[0];
//                    String fileName = extraString[1];
//                    SaveImageByFileFormat(fileFormat, fileName);
//                } else
//                    showToast("Cancelled!");
//                break;
//        }
//    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}

