package com.FingerprintCapture;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.FingerprintCapture.Utilities.Constants;
import com.futronictech.Scanner;
import com.futronictech.UsbDeviceDataExchangeImpl;


public class FPScan {
    private final Handler mHandler;
    private ScanThread mScanThread;
    private UsbDeviceDataExchangeImpl ctx = null;
    
    public FPScan(UsbDeviceDataExchangeImpl context, Handler handler ) {
        mHandler = handler;
        ctx = context;
    }

    public synchronized void start() {
        if (mScanThread == null) {
        	mScanThread = new ScanThread();
        	mScanThread.start();
        }
    }
    
    public synchronized void stop() {
        if (mScanThread != null) {mScanThread.cancel(); mScanThread = null;}
    }
    
    private class ScanThread extends Thread {
    	private boolean bGetInfo;
    	private Scanner devScan = null;
    	private String strInfo;
    	private int mask, flag;
    	private int errCode;
    	private boolean bRet;
    	
        public ScanThread() {
        	bGetInfo=false;
        	devScan = new Scanner();
        	/******************************************
        	// By default, a directory of "/mnt/sdcard/Android/" is necessary for libftrScanAPI.so to work properly
        	// in case you want to change it, you can set it by calling the below function
        	String SyncDir =  "/mnt/sdcard/test/";  // YOUR DIRECTORY
        	if( !devScan.SetGlobalSyncDir(SyncDir) )
        	{
                mHandler.obtainMessage(FtrConstants.FPScanConstantsActivity.MESSAGE_SHOW_MSG, -1, -1, devScan.GetErrorMessage()).sendToTarget();
                mHandler.obtainMessage(FtrConstants.FPScanConstantsActivity.MESSAGE_ERROR).sendToTarget();
           	    devScan = null;
	            return;
        	}
        	*******************************************/
        }

        public void run() {
            while (!Constants.FPScanConstants.mStop)
            {
            	if(!bGetInfo)
            	{
            		Log.i("FUTRONIC", "Run fp scan");
            		devScan.SetLogOptions(devScan.FTR_LOG_MASK_TO_FILE, devScan.FTR_LOG_LEVEL_OPTIMAL);
            		boolean bRet;
         	        if( Constants.FPScanConstants.mUsbHostMode )
         	        	bRet = devScan.OpenDeviceOnInterfaceUsbHost(ctx);
         	        else
         	        	bRet = devScan.OpenDevice();
                    if( !bRet )
                    {
                        //Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
                    	if(Constants.FPScanConstants.mUsbHostMode)
                    		ctx.CloseDevice();
                        mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_SHOW_MSG, -1, -1, devScan.GetErrorMessage()).sendToTarget();
                        mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_ERROR).sendToTarget();
                        return;
                    }

            		if( !devScan.GetImageSize() )
	    	        {
	    	        	mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_SHOW_MSG, -1, -1, devScan.GetErrorMessage()).sendToTarget();
	    	        	if( Constants.FPScanConstants.mUsbHostMode )
	    	        		devScan.CloseDeviceUsbHost();
	    	        	else
	    	        		devScan.CloseDevice();
                        mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_ERROR).sendToTarget();
	    	            return;
	    	        }

	    	        Constants.FPScanConstants.InitFingerPictureParameters(devScan.GetImageWidth(), devScan.GetImaegHeight());

	    	        strInfo = devScan.GetVersionInfo();
    	        	mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_SHOW_SCANNER_INFO, -1, -1, strInfo).sendToTarget();
	    	        bGetInfo = true;
             	}
                //set options
                flag = 0;
                mask = devScan.FTR_OPTIONS_DETECT_FAKE_FINGER | devScan.FTR_OPTIONS_INVERT_IMAGE;
                if(Constants.FPScanConstants.mLFD)
                	flag |= devScan.FTR_OPTIONS_DETECT_FAKE_FINGER;
                if( Constants.FPScanConstants.mInvertImage)
                	flag |= devScan.FTR_OPTIONS_INVERT_IMAGE;
                if( !devScan.SetOptions(mask, flag) )
    	        	mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_SHOW_MSG, -1, -1, devScan.GetErrorMessage()).sendToTarget();
                // get frame / image2
                long lT1 = SystemClock.uptimeMillis();
                if( Constants.FPScanConstants.mFrame )
                	bRet = devScan.GetFrame(Constants.FPScanConstants.mImageFP);
                else
                	bRet = devScan.GetImage2(4, Constants.FPScanConstants.mImageFP);
                if( !bRet )
                {
                	mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_SHOW_MSG, -1, -1, devScan.GetErrorMessage()).sendToTarget();
                	errCode = devScan.GetErrorCode();
                	if( errCode != devScan.FTR_ERROR_EMPTY_FRAME && errCode != devScan.FTR_ERROR_MOVABLE_FINGER &&  errCode != devScan.FTR_ERROR_NO_FRAME )
                	{
	    	        	if( Constants.FPScanConstants.mUsbHostMode )
	    	        		devScan.CloseDeviceUsbHost();
	    	        	else
	    	        		devScan.CloseDevice();
                        mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_ERROR).sendToTarget();
	    	            return;
                	}
                }
                else
                {
                	if( Constants.FPScanConstants.mFrame )
                		strInfo = String.format("OK. GetFrame time is %d(ms)",  SystemClock.uptimeMillis()-lT1);
                	else
                		strInfo = String.format("OK. GetImage2 time is %d(ms)",  SystemClock.uptimeMillis()-lT1);
                	mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_SHOW_MSG, -1, -1, strInfo ).sendToTarget();
                }
				synchronized (Constants.FPScanConstants.mSyncObj)
                {
					//show image
					mHandler.obtainMessage(Constants.FingerPrintScannerConstants.MESSAGE_SHOW_IMAGE).sendToTarget();
					try {
						Constants.FPScanConstants.mSyncObj.wait(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
            //close device
        	if( Constants.FPScanConstants.mUsbHostMode )
        		devScan.CloseDeviceUsbHost();
        	else
        		devScan.CloseDevice();
        }

        public void cancel() {
        	Constants.FPScanConstants.mStop = true;
        	try {
        		synchronized (Constants.FPScanConstants.mSyncObj)
		        {
        			Constants.FPScanConstants.mSyncObj.notifyAll();
		        }        		
        		this.join();	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       	      	           	
        }
    }    
}
