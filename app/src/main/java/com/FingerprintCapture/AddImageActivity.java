package com.FingerprintCapture;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.FingerprintCapture.BioServerWebServices.BioServerServices.EnrollSuccess;
import com.FingerprintCapture.Services.AllUrl;
import com.FingerprintCapture.Services.RestClient;
import com.FingerprintCapture.Services.UserApi;
import com.FingerprintCapture.Utilities.Constants;
import com.FingerprintCapture.Utilities.ProgressRequestBody;
import com.FingerprintCapture.Utilities.SharedPrefUtility;
import com.FingerprintCapture.application.FingerprintCaptureApplication;
import com.FingerprintCapture.models.response.CustomerData;
import com.FingerprintCapture.models.response.UploadImageResponse;
import com.bumptech.glide.Glide;
import com.futronictech.UsbDeviceDataExchangeImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddImageActivity extends AppCompatActivity implements View.OnClickListener, EnrollSuccess,
        ProgressRequestBody.UploadCallbacks{
 //   public static boolean mFrame = true;
//    public static boolean mLFD = false;
//    public static boolean mInvertImage = false;
    Button mCameraButton, mButtonScan;
 //   public static boolean mStop = false;
    private FPScan mFPScan = null;
    private final int REQUEST_CAMERA = 101;
    private File photoFile;
    private boolean isDefaultImage = true;
    private static ImageView userImage;
    private Uri imageUri, thumbNailUri;
    private int THUMBNAIL_SIZE = 100;
    private Uri myUri, tempUri;
    private File imgFile;
    private String realPath;
    private Uri mCapturedImageURI;
    Button mMorePrintsButton, mUploadButton;
    public  CustomerData mCustomerData;
    ImageButton mBackButton;
    private View mRootView;
    private Boolean mIsImage,mUploadPrint;
    private String mBitmapImage="";
    private File imageFile, thumbNailfile;
    private String mCustomerId;
//    private LinearLayout progressLayout;
    int progressStatus = 0;
    private Handler handler = new Handler();
 //   public static boolean mUsbHostMode = true;
   // ProgressDialog pd;

    private UsbDeviceDataExchangeImpl usb_host_ctx = null;

    private ProgressBar mProgress;
    private final int minProgress = 0, maxProgress = 99, THREAD_SLEEP_TIME = 200;

    private TextView tvProgress;

    private final String status = "status";
    private boolean isUpdateDataCall = false, isProcessing = false;

    public static final int MESSAGE_SHOW_MSG = 1;
    public static final int MESSAGE_SHOW_SCANNER_INFO = 2;
    public static final int MESSAGE_SHOW_IMAGE = 3;
    public static final int MESSAGE_ERROR = 4;
    public static final int MESSAGE_TRACE = 5;

//    public static byte[] mImageFP = null;
//    public static Object mSyncObj = new Object();

  //  public static int Constants.FPScanConstants.mImageWidth = 0;
   // public static int Constants.FPScanConstants.mImageHeight = 0;
    //private static int[] Constants.FPScanConstants.mPixels = null;
    //private static Bitmap Constants.FPScanConstants.mBitmapFP = null;
   // private static Canvas Constants.FPScanConstants.mCanvas = null;
   // private static Paint Constants.FPScanConstants.mPaint = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        initViews();
        initListeners();
        setDataToActivity();
        usb_host_ctx = new UsbDeviceDataExchangeImpl(this, mHandler);

    }

    private void setDataToActivity() {

        if (getIntent() != null && getIntent().getParcelableExtra(Constants.sCUSTOMER_DATA) != null) {
            mCustomerData = getIntent().getParcelableExtra(Constants.sCUSTOMER_DATA);
            isUpdateDataCall = true;
            mUploadPrint=mCustomerData.getFingerPrint()==0;
        }


        /*mCustomerData = UploadData.mCustomerData;
        isUpdateDataCall = true;*/
    }

    public void initViews() {
        mCameraButton = findViewById(R.id.btn_camera);
        mButtonScan = findViewById(R.id.btn_scan);
        userImage = findViewById(R.id.user_image);
        mMorePrintsButton = findViewById(R.id.more_prints);
        mBackButton = findViewById(R.id.action_bar_back_button);
        mUploadButton = findViewById(R.id.btn_upload_data);
        mRootView = findViewById(R.id.root_layout);
        mCustomerId = Constants.sEMPTY_STRING + SharedPrefUtility.getCustomerId(this);

        Constants.FPScanConstants.mUsbHostMode = true;
    }

    public void initListeners() {

        mCameraButton.setOnClickListener(this);
        mButtonScan.setOnClickListener(this);
        mMorePrintsButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_camera:
                openCamera();
                mIsImage=true;
                break;
            case R.id.more_prints:
                openGalleryActivity(mCustomerData);
                break;
            case R.id.action_bar_back_button:
                onBackPressed();
                finish();
                break;
            case R.id.btn_upload_data:
                uploadImage();
                break;
            case R.id.btn_scan:
                startScan();
                mIsImage=false;
                break;
        }
    }

    private void startScan() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        Constants.FPScanConstants.InitFingerPictureParameters(userImage.getWidth(), userImage.getHeight());
        if (mFPScan != null) {
            Constants.FPScanConstants.mStop = true;
            mFPScan.stop();
        }
        Constants.FPScanConstants.mStop = false;
        if (Constants.FPScanConstants.mUsbHostMode && usb_host_ctx != null) {
            usb_host_ctx.CloseDevice();
            if (usb_host_ctx.OpenDevice(0, true)) {
                if (StartScan()) {
                    mButtonScan.setEnabled(false);
                }
            } else {
                if (!usb_host_ctx.IsPendingOpen()) {
                    FingerprintCaptureApplication.getApplicationInstance().showToast(getString(R.string.device_error));
                }
            }
        } else {
            if (StartScan()) {
                mButtonScan.setEnabled(false);
            }
        }
    }

    // The Handler that gets information back from the FPScanNewN
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_MSG:
                    String showMsg = (String) msg.obj;
                    break;
                case MESSAGE_SHOW_SCANNER_INFO:
                    String showInfo = (String) msg.obj;
                    break;
                case MESSAGE_SHOW_IMAGE:
                    ShowBitmap();
                    break;
                case MESSAGE_ERROR:
                    mButtonScan.setEnabled(true);
                    mFPScan.stop();
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_ALLOW_DEVICE:
                    if (usb_host_ctx.ValidateContext()) {
                        if (StartScan()) {
                            mButtonScan.setEnabled(false);
                        } else {
                            mButtonScan.setEnabled(true);
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


    private boolean StartScan() {
        mFPScan = new FPScan(usb_host_ctx, mHandler);
        mFPScan.start();
        return true;
    }

//    public static void InitFingerPictureParameters(int wight, int height) {
//
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


    private void uploadImage() {
        if (isDefaultImage) {
            FingerprintCaptureApplication.getApplicationInstance().showSnackBar(mRootView, getString(R.string.please_select_an_image));

        } else {
            Constants.FPScanConstants.mStop = true;
            if (mFPScan != null) {
                mFPScan.stop();
                mFPScan = null;

            }
            mButtonScan.setEnabled(true);
            if (FingerprintCaptureApplication.getApplicationInstance().isConnected()) {
                userImage.setClickable(false);
                mCameraButton.setEnabled(false);
                mButtonScan.setEnabled(false);
                mMorePrintsButton.setEnabled(false);
                mUploadButton.setEnabled(false);
                uploadService(); // uploadImage
              //  callProgress();
            } else {
                FingerprintCaptureApplication.getApplicationInstance().showSnackBar(mRootView,
                        getString(R.string.not_connected_to_network));

            }
        }
    }


    @NonNull
    private RequestBody createPartFromString(String string) {
        return RequestBody.create(MultipartBody.FORM, string);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath().toString());
        imageFile = file;
        System.out.println("getfile " + file.toString() + " " + file.getName());
        RequestBody requestBody = RequestBody.create(MediaType.parse(fileUri.getPath().toString()), file);
        return MultipartBody.Part.createFormData(partName, fileUri.getPath(), requestBody);
    }


    private void uploadService()  {

//        progressLayout = (LinearLayout) findViewById(R.id.progress_bar_layout_upload);
//        progressLayout.setVisibility(View.GONE);
//        tvProgress = (TextView) findViewById(R.id.tv_progress_upload);
//        mProgress = (ProgressBar) findViewById(R.id.circularProgressbar_upload);
//        mProgress.setProgress(0);

        mBackButton.setEnabled(false);
        FingerprintCaptureApplication.getApplicationInstance().showProgress(this, getString(R.string.saving_data));
        System.out.println("mcaptured image uri" + mCapturedImageURI + "hello upload");
        Retrofit retrofit = RestClient.build(AllUrl.baseUrl);
        UserApi userApi = retrofit.create(UserApi.class);
        if(Constants.FPScanConstants.mBitmapFP!=null && !mIsImage ) {
            mBitmapImage=VerifyFingerPrint.convertToBase64fromBitmap(Constants.FPScanConstants.mBitmapFP);
        }
        /*try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            mBitmapImage=VerifyFingerPrint.convertToBase64fromBitmap(bitmap);
        }catch (Exception ex){
          Log.e("print",ex.getMessage());
        }
*/
        isProcessing = true;

        MultipartBody.Part filePart=null;
        try {
            File file = new File(imageUri.getPath().toString());
            ProgressRequestBody fileBody = new ProgressRequestBody(file, this,streamFileType(file));
            filePart =
                    MultipartBody.Part.createFormData("image", file.getName(), fileBody);
        }catch (Exception e){

        }



        Call<JsonObject> call = userApi.uploadData(createPartFromString(mCustomerId),
                createPartFromString(mCustomerData.getName()),
                createPartFromString(mCustomerData.getDob()),
                createPartFromString(mCustomerData.getAgentId()),
                createPartFromString(mCustomerData.getId()),
                createPartFromString(mCustomerData.getFirstName()),
                createPartFromString(mCustomerData.getEmail()),
                createPartFromString(mBitmapImage),
                filePart,
                /*prepareFilePart("image", imageUri),*/
                prepareFilePart("thumb_image", thumbNailUri)

        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {
                if (response.body().get(status).toString().equals("200")) {
                     final UploadImageResponse uploadImageResponse = new Gson().fromJson(response.body(), UploadImageResponse.class);
                    CustomerData customerData = uploadImageResponse.getCustomerData().get(0);
//                    String fingerPrintId ;

                    //send customer id for old customers not having upd id
//                    fingerPrintId=customerData.getUpdId()==null?customerData.getId():customerData.getUpdId();
//                    if ( !mIsImage ) {
//                        if (progressLayout != null) {
//                            progressLayout.setVisibility(View.GONE);
//                        }
//                        BioServerEnrollmentEventHandler bioServerEnrollmentEventHandler = new BioServerEnrollmentEventHandler(fingerPrintId, mBitmapImage,AddImageActivity.this);
//                        bioServerEnrollmentEventHandler.doEnrollment(AddImageActivity.this,uploadImageResponse);
//                    }
//                    else {
//
//                        success(uploadImageResponse);
//                    }
                    success(uploadImageResponse);
                } else {
                   fail(response.body().get(status).toString());
//                    Toast.makeText(AddImageActivity.this,"OnFail"+response.body().get(status).toString(),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                fail(t.getMessage());
//                Toast.makeText(AddImageActivity.this,"OnFailure"+t,Toast.LENGTH_LONG).show();
               }
        });


    }
    @Override
    public void fail(String message) {
        mBackButton.setEnabled(true);
//        progressLayout.setVisibility(View.GONE);
        userImage.setClickable(true);
        mCameraButton.setEnabled(true);
        mButtonScan.setEnabled(true);
        isProcessing = false;
        mMorePrintsButton.setEnabled(true);
        userImage.setClickable(true);

        mUploadButton.setEnabled(true);
        FingerprintCaptureApplication.getApplicationInstance().hideProgress();
        FingerprintCaptureApplication.getApplicationInstance().showSnackBar(mRootView,
                getString(R.string.unable_to_connect));

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddImageActivity.this);
        alertDialog.setMessage(getString(R.string.failed_to_upload));
        alertDialog.setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                uploadImage();
            }
        });

        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//                        date.setText("");
                userImage.setImageResource(R.drawable.image_upload_icon);
                isDefaultImage = true;
//                        date.setEnabled(true);
            }
        });
        alertDialog.show();

    }

    @Override
    public void success(UploadImageResponse uploadImageResponse) {
        mBackButton.setEnabled(true);

            //progressDialog.dismiss();
//        if (progressLayout!=null)
//            progressLayout.setVisibility(View.GONE);
            FingerprintCaptureApplication.getApplicationInstance().setUpdated(true);
            isProcessing = false;
            FingerprintCaptureApplication.getApplicationInstance().hideProgress();
/*
                    FingerprintCaptureApplication.getApplicationInstance().showSnackBar(mRootView, getString(R.string.upload_successful));
*/
            AlertDialog.Builder builder = new AlertDialog.Builder(AddImageActivity.this);
            builder.setMessage(getString(R.string.upload_successful))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok_message), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (isUpdateDataCall) {
                                uploadImageResponse.getCustomerData().get(0).setFingerPrint(mIsImage ? 0 : 1);
                                openGalleryActivity(uploadImageResponse.getCustomerData().get(0));
                                finish();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.sCUSTOMER_DATA_ARRAY, uploadImageResponse);
                                setResult(Constants.RequestCode.sCUSTOMER_IMAGE_SCAN_ADDED, intent);
                                finish();
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            if (!isFinishing()) {
                alert.show();
                userImage.setImageResource(R.drawable.image_upload_icon);
                isDefaultImage = true;
                userImage.setClickable(true);
//                        date.setEnabled(true);
                mUploadButton.setEnabled(true);
            }
        alert.show();
    }

    private void openCamera() {
        Constants.FPScanConstants.mStop = true;
        if (mFPScan != null) {
            mFPScan.stop();
            mFPScan = null;
        }
        mButtonScan.setEnabled(true);
        EasyImage.configuration(AddImageActivity.this)
                .setImagesFolderName("EasyImage sample");
        EasyImage.openCamera(AddImageActivity.this, REQUEST_CAMERA);
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
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    photoFile = EasyImage.lastlyTakenButCanceledPhoto(getApplicationContext());
                    Glide.with(AddImageActivity.this).load(Uri.fromFile(photoFile)).into(userImage);
                    isDefaultImage = false;
                    imageUri = Uri.fromFile(photoFile);
                    bitmapOfImage(imageUri);
                }
            }
        });
//        }

        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    isDefaultImage = false;
                    if (resultCode == RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        Uri uri = data.getData();
                        myUri = uri;
                        tempUri = getImageUri(getApplicationContext(), photo);
                        System.out.println(uri + "    uri uri " + tempUri);
                        imgFile = new File(getRealPathFromURI(tempUri));
                        mCapturedImageURI = tempUri;
                        realPath = getRealPathFromUri(getApplicationContext(), tempUri);
                        System.out.println(realPath + "  hell of it" + mCapturedImageURI);
                        Glide.with(AddImageActivity.this).load(Uri.fromFile(imgFile)).into(userImage);
                    }
                    break;
                }
        }
    }

    private void bitmapOfImage(Uri uri) {
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(uri.getPath()),
                THUMBNAIL_SIZE, THUMBNAIL_SIZE);

        //rotate bitmap
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        //create new rotated bitmap
        thumbImage = Bitmap.createBitmap(thumbImage, 0, 0,thumbImage.getWidth(), thumbImage.getHeight(), matrix, true);


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

    private void openGalleryActivity(CustomerData customerData) {
        Intent intent = new Intent(this, AgentGalleryActivity.class);
        intent.putExtra(Constants.sCUSTOMER_DATA_ARRAY, customerData);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.FPScanConstants.mStop = true;
        if (mFPScan != null) {
            mFPScan.stop();
            mFPScan = null;
        }
        if (usb_host_ctx != null) {
            usb_host_ctx.CloseDevice();
            usb_host_ctx.Destroy();
            usb_host_ctx = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Constants.FPScanConstants.mStop = true;
//        if (mFPScan != null) {
//            mFPScan.stop();
//            mFPScan = null;
//        }
//        if (usb_host_ctx != null) {
//            usb_host_ctx.CloseDevice();
//            usb_host_ctx.Destroy();
//            usb_host_ctx = null;
//        }
//        if (mHandler != null) {
//            mHandler.removeCallbacksAndMessages(null);
//        }
    }


    @Override
    public void onProgressUpdate(int percentage, Boolean showProgress_) {
//        if (showProgress_)
//            progressLayout.setVisibility(View.VISIBLE);
//        mProgress.setProgress(percentage);
//        tvProgress.setText(percentage+"%");
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
//        mProgress.setProgress(100);
//        tvProgress.setText("100%");
    }

    public String  streamFileType(File file)
    {

        String exsistingFileName  = file.getName();
        if (exsistingFileName.endsWith(".jpg"))
        {
            return "image/jpg";
        }
        if(exsistingFileName.endsWith(".JPG")){

            return "image/jpg";
        }
        if (exsistingFileName.endsWith(".png")) {

            return "image/png";

        }
        if (exsistingFileName.endsWith(".PNG")) {

            return "image/png";
        }
        if (exsistingFileName.endsWith(".gif")) {

            return "image/gif";

        }
        if (exsistingFileName.endsWith(".GIF")) {

            return "image/gif";

        }
        if (exsistingFileName.endsWith(".jpeg")) {

            return "image/jpeg";

        }
        if (exsistingFileName.endsWith(".JPEG")) {

            return "image/jpeg";

        }
        if(exsistingFileName.endsWith(".mp3")){

            return "audio/mp3";

        }
        if(exsistingFileName.endsWith(".MP3")){

            return "audio/mp3";

        }
        if (exsistingFileName.endsWith(".mp4")) {

            return "video/mp4";

        }
        if (exsistingFileName.endsWith(".avi"))
        {
            return "video/.avi";

        }
        if (exsistingFileName.endsWith(".ogg")) {

            return "video/ogg";

        }
        if (exsistingFileName.endsWith(".3gp")) {

            return "video/3gp";
        }

        return "";
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognise a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }

        return degree;
    }
}
