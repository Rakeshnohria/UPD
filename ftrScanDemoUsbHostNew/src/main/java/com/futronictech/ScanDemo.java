package com.futronictech;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicInteger;


public class ScanDemo extends Activity {

    private static Button mButtonScan;
    private static Button mButtonStop;
    //    private static TextView mScannerInfo;
    private static ImageView mFingerImage, finalScan;


    public static boolean mStop = false;
    public static boolean mFrame = true;
    public static boolean mLFD = false;
    public static boolean mInvertImage = false;

    public static final int MESSAGE_SHOW_MSG = 1;
    public static final int MESSAGE_SHOW_SCANNER_INFO = 2;
    public static final int MESSAGE_SHOW_IMAGE = 3;
    public static final int MESSAGE_ERROR = 4;
    public static final int MESSAGE_TRACE = 5;

    public static byte[] mImageFP = null;
    public static Object mSyncObj = new Object();

    public static int mImageWidth = 0;
    public static int mImageHeight = 0;
    private static int[] mPixels = null;
    private static Bitmap mBitmapFP = null;
    private static Canvas mCanvas = null;
    private static Paint mPaint = null;
    TextView textView123;
    private FPScanNew mFPScan = null;
    //
    public static boolean mUsbHostMode = true;
    final Handler handlerForCountDown = new Handler();
    java.util.concurrent.atomic.AtomicInteger n = new AtomicInteger(3);
    // Intent request codes
    private static final int REQUEST_FILE_FORMAT = 1;
    private UsbDeviceDataExchangeImpl usb_host_ctx = null;

    public static void InitFingerPictureParameters(int wight, int height) {
        mImageWidth = wight;
        mImageHeight = height;

        mImageFP = new byte[mImageWidth * mImageHeight];
        mPixels = new int[mImageWidth * mImageHeight];

        mBitmapFP = Bitmap.createBitmap(wight, height, Config.RGB_565);

        mCanvas = new Canvas(mBitmapFP);
        mPaint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        mPaint.setColorFilter(f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_demo);

        mFrame = true;
        mUsbHostMode = true;
        mLFD = mInvertImage = false;
        mButtonScan = (Button) findViewById(R.id.scan);
        mButtonStop = (Button) findViewById(R.id.upload);
//        mScannerInfo = (TextView) findViewById(R.id.tvScannerInfo);
        mFingerImage = (ImageView) findViewById(R.id.scan_image);
        finalScan = (ImageView) findViewById(R.id.final_image);
        textView123 = (TextView) findViewById(R.id.textView123);
        usb_host_ctx = new UsbDeviceDataExchangeImpl(this, mHandler);
        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStop = true;
                if (mFPScan != null) {
                    mFPScan.stop();
                    mFPScan = null;

                }
                mButtonScan.setEnabled(true);
                uploadImage();
            }
        });
    }

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
                        mButtonStop.setEnabled(true);
                    }
                }
            }
        };
        handlerForCountDown.postDelayed(counter, 1000);
    }

    private void startScan() {
        InitFingerPictureParameters(mFingerImage.getWidth(), mFingerImage.getHeight());
        if (mFPScan != null) {
            mStop = true;
            mFPScan.stop();

        }
        mStop = false;
        if (mUsbHostMode) {
            usb_host_ctx.CloseDevice();
            if (usb_host_ctx.OpenDevice(0, true)) {
//                if (StartScan()) {
//                    mButtonScan.setEnabled(false);
//                    mButtonStop.setEnabled(true);
//                }
                showCountDown();
            } else {
                if (!usb_host_ctx.IsPendingOpen()) {
                    showToast("Can not start scan operation.\nCan't open scanner device");
                }
            }
        } else {
            if (StartScan()) {
                mButtonScan.setEnabled(false);
                mButtonStop.setEnabled(true);
            }
        }
    }

    private void uploadImage() {
        Bitmap bm = ((BitmapDrawable) mFingerImage.getDrawable()).getBitmap();
        finalScan.setImageBitmap(bm);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStop = true;
        if (mFPScan != null) {
            mFPScan.stop();
            mFPScan = null;
        }
        usb_host_ctx.CloseDevice();
        usb_host_ctx.Destroy();
        usb_host_ctx = null;
    }

    private boolean StartScan() {
        mFPScan = new FPScanNew(usb_host_ctx, mHandler);
        mFPScan.start();
        return true;
    }

    private void SaveImage() {
        Intent serverIntent = new Intent(this, SelectFileFormatActivity.class);
        startActivityForResult(serverIntent, REQUEST_FILE_FORMAT);
    }

    private void SaveImageByFileFormat(String fileFormat, String fileName) {
        if (fileFormat.compareTo("WSQ") == 0)    //save wsq file
        {
            Scanner devScan = new Scanner();
            boolean bRet;
            if (mUsbHostMode)
                bRet = devScan.OpenDeviceOnInterfaceUsbHost(usb_host_ctx);
            else
                bRet = devScan.OpenDevice();
            if (!bRet) {
                showToast(devScan.GetErrorMessage());
                return;
            }
            byte[] wsqImg = new byte[mImageWidth * mImageHeight];
            long hDevice = devScan.GetDeviceHandle();
            ftrWsqAndroidHelper wsqHelper = new ftrWsqAndroidHelper();
            if (wsqHelper.ConvertRawToWsq(hDevice, mImageWidth, mImageHeight, 2.25f, mImageFP, wsqImg)) {
                File file = new File(fileName);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(wsqImg, 0, wsqHelper.mWSQ_size);    // save the wsq_size bytes data to file
                    out.close();
                    showToast("Image is saved as " + fileName);
                } catch (Exception e) {
                    showToast("Exception in saving file");
                }
            } else
                showToast("Failed to convert the image!");
            if (mUsbHostMode)
                devScan.CloseDeviceUsbHost();
            else
                devScan.CloseDevice();
            return;
        }
        // 0 - save bitmap file
        File file = new File(fileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            //mBitmapFP.compress(Bitmap.CompressFormat.PNG, 90, out);
            MyBitmapFile fileBMP = new MyBitmapFile(mImageWidth, mImageHeight, mImageFP);
            out.write(fileBMP.toBytes());
            out.close();
            showToast("Image is saved as " + fileName);
        } catch (Exception e) {
            showToast("Exception in saving file");
        }
    }

    // The Handler that gets information back from the FPScanNew
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
                    mButtonStop.setEnabled(false);
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_ALLOW_DEVICE:
                    if (usb_host_ctx.ValidateContext()) {
                        if (StartScan()) {
                            mButtonScan.setEnabled(false);
                            mButtonStop.setEnabled(true);
                        }
                    } else
                        showToast("Can't open scanner device");
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_DENY_DEVICE:
                    showToast("User deny scanner device");
                    break;
            }
        }
    };

    private static void ShowBitmap() {
        for (int i = 0; i < mImageWidth * mImageHeight; i++) {
            mPixels[i] = Color.rgb(mImageFP[i], mImageFP[i], mImageFP[i]);
        }
        mCanvas.drawBitmap(mPixels, 0, mImageWidth, 0, 0, mImageWidth, mImageHeight, false, mPaint);
        mFingerImage.setImageBitmap(mBitmapFP);
        mFingerImage.invalidate();
        synchronized (mSyncObj) {
            mSyncObj.notifyAll();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "On Result", Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case REQUEST_FILE_FORMAT:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the file format
                    String[] extraString = data.getExtras().getStringArray(SelectFileFormatActivity.EXTRA_FILE_FORMAT);
                    String fileFormat = extraString[0];
                    String fileName = extraString[1];
                    SaveImageByFileFormat(fileFormat, fileName);
                } else
                    showToast("Cancelled!");
                break;
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
