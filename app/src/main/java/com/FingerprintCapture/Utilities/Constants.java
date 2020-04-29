package com.FingerprintCapture.Utilities;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.FingerprintCapture.FPScan;
import com.futronictech.UsbDeviceDataExchangeImpl;

public class Constants {
    public static final String sEMPTY_STRING = "";
    public static final String sSPACE = " ";
    public static final String sCOLON = ":";
    public static final int sHANDLER_DELAY = 2000;
    public static final int sHANLDER_DELAY_SEARCH = 500;
    public static final int sLIMIT = 10;
    public static final String sCUSTOMER_DATA = "customerData";
    public static final String sCUSTOMER_DATA_ARRAY = "customerDataArray";
    //public static final String sZANDESK_ACCOUNT_KEY = "HfMGj6fXe5rin0YpQAR1cdZi5wmUZmDL"; Dummy
    public static final String sZANDESK_ACCOUNT_KEY = "5LGGRDhWAkkYDQ3D8FHJHCgjnlPPB8DJ"; // UPD KEY
    public static final String sFAQ_URL = "https://updurns.zendesk.com/hc/en-us/sections/360003493311-UPD-Prints";
    public static final String sIsSubscribed="isSubscribed";
    public static final int sSubscribed=1;
    public static final int sNotSubscribed=0;
    public static final int sVerified=1;
    public static final int sNotVerified=0;

    public class SharedPreferences {
        public static final String sKEY_FOR_SHARED_PREFERENCES = "sharedPreferences";
        public static final String sCUSTOMER_ID = "customer_id";
        public static final String sIsSubscibed="is_subscribed_user";
        public static final String sLOGIN_STATUS = "login_status";
        public static final String sEMAIL_ADDRESS = "emailAddress";
        public static final String sACCOUNTS_LIST = "accountsList";
    }

    public class RequestCode {
        public static final int sCUSTOMER_DATA_CODE = 100;
        public static final int sCUSTOMER_DATA_NEW=110;
        public static final int sCUSTOMER_IMAGE_SCAN_ADDED=120;
        public static final int sGALLERY_DATA_CODE = 200;
    }
     public static String sIS_ADD_ACCOUNT="isAddAccount";

    /*
    *
    * constants for finger Print Verification
     */
    public static final String sCUSTOMER_DATA_Verify="verifyCustomer";
    public static final int sVeriFication=300;

    public static class FingerPrintScannerConstants
    {
        public static final int MESSAGE_SHOW_MSG = 1;
        public static final int MESSAGE_SHOW_SCANNER_INFO = 2;
        public static final int MESSAGE_SHOW_IMAGE = 3;
        public static final int MESSAGE_ERROR = 4;
        public static final int MESSAGE_TRACE = 5;
    }
    public static class Fingerprint {
        public static final int sFingerPrintResponse = 103;
        public static final String sADDFINGER_PRINT = "FingerprintAdd";
        public static final String sSAVEFINGER_PRINT = "FingerprintSave";
        public static final String sVERIFYFINGER_PRINT = "FingerprintVerify";
        public static final String sVERIFIEDFINGER_PRINT = "FingerprintVerified";
        public static final int sVerified=111;
        public static final int sVerify=112;
        public static final int sAddFingerPrint =113;
        public static final int sFingerPrintAdded=114;
        public static final String sFINGERPRINT_ID = "FingerprintUUID";

    }
    /*
    *
    * constants for fpScan
     */
    public static class FPScanConstants{
        public static byte[] mImageFP = null;
        public static Object mSyncObj = new Object();
        public static boolean mStop = false;
        public static boolean mFrame = true;
        public static boolean mLFD = false;
        public static boolean mInvertImage = false;
        public static int mImageWidth = 0;
        public static int mImageHeight = 0;
        public static int[] mPixels = null;
        public static Bitmap mBitmapFP = null;
        public static Canvas mCanvas = null;
        public static Paint mPaint = null;
       // public FPScan mFPScan = null;
        public static boolean mUsbHostMode = true;
       // public UsbDeviceDataExchangeImpl usb_host_ctx = null;

        public static void InitFingerPictureParameters(int wight, int height) {
            mImageWidth = wight;
            mImageHeight = height;
            mImageFP = new byte[mImageWidth * mImageHeight];
            mPixels = new int[mImageWidth * mImageHeight];
            mBitmapFP = Bitmap.createBitmap(wight, height, Bitmap.Config.RGB_565);
            mCanvas = new Canvas(mBitmapFP);
            mPaint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            mPaint.setColorFilter(f);
        }


    }
}




