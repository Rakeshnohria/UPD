package com.FingerprintCapture.Utilities;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;


public class ProgressRequestBody extends RequestBody
{
    private File mFile;
    private String mPath ,type = "";
    private UploadCallbacks mListener;
    private Boolean showProgress = true;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface  UploadCallbacks {
        void onProgressUpdate(int percentage,Boolean showProgress);
        void onError();
        void onFinish();
    }

    public ProgressRequestBody(final File file, final  UploadCallbacks listener , String type)
    {
        mFile = file;
        mListener = listener;
        this.type = type;
        showProgress = true;
    }

    @Override
    public MediaType contentType() {
        // i want to upload only images
        return MediaType.parse(type);
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws FileNotFoundException {

        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        if (showProgress)
            showProgress = false;
        else
            showProgress = true;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {
                // update progress on UI thread
                handler.post(new ProgressUpdater(uploaded, fileLength,showProgress));

                uploaded += read;
                sink.write(buffer, 0, read);


            }
            in.close();
        }
        catch (Exception e){
        }
        finally
        {

        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        private Boolean showProgress_;
        public ProgressUpdater(long uploaded, long total,Boolean showProgress) {
            mUploaded = uploaded;
            mTotal = total;
            showProgress_ = showProgress;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int)(100 * mUploaded / mTotal),showProgress_);

            if (mUploaded>=mTotal-1000)
                mListener.onFinish();
        }
    }
}




