package com.FingerprintCapture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.FingerprintCapture.Utilities.Constants;
import com.futronictech.UsbDeviceDataExchangeImpl;

/**
 * Created by Lakshmi on 15/05/18.
 */

public class ScanFingerPrint {
    //  public static byte[] Constants.FPScanConstants.mImageFP = null;
    // public static Object Constants.FPScanConstants.mSyncObj = new Object();
    // public static boolean Constants.FPScanConstants.mStop = false;
    // public static boolean Constants.FPScanConstants.mFrame = true;
    //  public static boolean Constants.FPScanConstants.mLFD = false;
    // public static boolean Constants.FPScanConstants.mInvertImage = false;
    //  public static int Constants.FPScanConstants.mImageWidth = 0;
    // public static int Constants.FPScanConstants.mImageHeight = 0;
    // private static int[] Constants.FPScanConstants.mPixels = null;
    //   private static Bitmap Constants.FPScanConstants.mBitmapFP = null;
    //   private static Canvas Constants.FPScanConstants.mCanvas = null;
    //   private static Paint Constants.FPScanConstants.mPaint = null;
    private FPScan mFPScan = null;
    // public static boolean Constants.FPScanConstants.mUsbHostMode = true;
    private UsbDeviceDataExchangeImpl usb_host_ctx = null;
    //End for scan data
    private Button mScanButton, mSaveButton, mVerifyButton;
    private ImageView mImageView;
    private Context context;

    public ScanFingerPrint(Context context, Button pTargetButton, Button pSaveButton, Button pVerifyButton, ImageView pImageView) {
        //  Constants.FPScanConstants.mFrame = true;
        //   Constants.FPScanConstants.mUsbHostMode = true;
        //   Constants.FPScanConstants.mLFD = Constants.FPScanConstants.mInvertImage = false;
        this.mImageView = pImageView;
        this.mScanButton = pTargetButton;
        this.mSaveButton = pSaveButton;
        this.mVerifyButton = pVerifyButton;
        this.context = context;
        usb_host_ctx = new UsbDeviceDataExchangeImpl(context, mHandler);
    }

    public void startScan() {
        Constants.FPScanConstants.InitFingerPictureParameters(mImageView.getWidth(), mImageView.getHeight());
        if (mFPScan != null) {
            Constants.FPScanConstants.mStop = true;
            mFPScan.stop();
        }
        Constants.FPScanConstants.mStop = false;
        if (Constants.FPScanConstants.mUsbHostMode) {
            usb_host_ctx.CloseDevice();
            if (usb_host_ctx.OpenDevice(0, true)) {
                if (StartScan()) {
                    buttonEnable(true);
                }
            } else {
                if (!usb_host_ctx.IsPendingOpen()) {
                    showToast(context.getString(R.string.device_error));
                    Log.e(String.valueOf(this.getClass()), context.getString(R.string.device_error));
                }
            }
        } else {
            if (StartScan()) {
                buttonEnable(true);
            }
        }
    }

    private void buttonEnable(Boolean flag) {
        mScanButton.setSelected(!flag);
        mScanButton.setEnabled(!flag);
        mSaveButton.setEnabled(flag);
        mSaveButton.setSelected(flag);
        mVerifyButton.setEnabled(flag);
        mVerifyButton.setSelected(flag);
    }
    // For scanning
//    public static void InitFingerPictureParameters(int wight, int height) {
//        Constants.FPScanConstants.mImageWidth = wight;
//        Constants.FPScanConstants.mImageHeight = height;
//        Constants.FPScanConstants.mImageFP = new byte[Constants.FPScanConstants.mImageWidth * Constants.FPScanConstants.mImageHeight];
//        Constants.FPScanConstants.mPixels = new int[Constants.FPScanConstants.mImageWidth * Constants.FPScanConstants.mImageHeight];
//        Constants.FPScanConstants.mBitmapFP = Bitmap.createBitmap(wight, height, Bitmap.Config.RGB_565);
//        Constants.FPScanConstants.mCanvas = new Canvas(Constants.FPScanConstants.mBitmapFP);
//        Constants.FPScanConstants.mPaint = new Paint();
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);
//        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
//        Constants.FPScanConstants.mPaint.setColorFilter(f);
//    }

    private boolean StartScan() {
        mFPScan = new FPScan(usb_host_ctx, mHandler);
        mFPScan.start();
        return true;
    }

    public void StopScan() {
        if (mFPScan != null) {
            mFPScan.stop();
        }
    }

    // The Handler that gets information back from the FPScan
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.FingerPrintScannerConstants.MESSAGE_SHOW_MSG:
                    String showMsg = (String) msg.obj;
                    break;
                case Constants.FingerPrintScannerConstants.MESSAGE_SHOW_SCANNER_INFO:
                    String showInfo = (String) msg.obj;
                    break;
                case Constants.FingerPrintScannerConstants.MESSAGE_SHOW_IMAGE:
                    ShowBitmap();
                    break;
                case Constants.FingerPrintScannerConstants.MESSAGE_ERROR:
                    buttonEnable(false);
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_ALLOW_DEVICE:
                    if (usb_host_ctx.ValidateContext()) {
                        if (StartScan()) {
                            buttonEnable(true);
                        }
                    } else {
                        showToast("Can't open scanner device");
                    }
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_DENY_DEVICE:
                    StopScan();
                    break;
            }
        }
    };

    private void ShowBitmap() {
        for (int i = 0; i < Constants.FPScanConstants.mImageWidth * Constants.FPScanConstants.mImageHeight; i++) {
            Constants.FPScanConstants.mPixels[i] = Color.rgb(Constants.FPScanConstants.mImageFP[i],
                    Constants.FPScanConstants.mImageFP[i], Constants.FPScanConstants.mImageFP[i]);
        }
        Constants.FPScanConstants.mCanvas.drawBitmap(Constants.FPScanConstants.mPixels, 0, Constants.FPScanConstants.mImageWidth, 0, 0, Constants.FPScanConstants.mImageWidth, Constants.FPScanConstants.mImageHeight, false, Constants.FPScanConstants.mPaint);
        mImageView.setImageBitmap(Constants.FPScanConstants.mBitmapFP);
        mImageView.invalidate();
        synchronized (Constants.FPScanConstants.mSyncObj) {
            Constants.FPScanConstants.mSyncObj.notifyAll();
        }
    }

    private static Toast toast = null;

    private void showToast(String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();

    }

    public void dispose() {
        if (usb_host_ctx != null) {
            Constants.FPScanConstants.mStop = true;
            if (mFPScan != null) {
                mFPScan.stop();
                mFPScan = null;
            }
            usb_host_ctx.CloseDevice();
            usb_host_ctx.Destroy();
            usb_host_ctx = null;
        }
    }
}
