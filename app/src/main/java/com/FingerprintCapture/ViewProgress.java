package com.FingerprintCapture;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ViewProgress extends RequestBody {
    private File mFile;
    private String mImgName;
    private UploadCallbacks mListener;
    private String content_type;
    public static Boolean flag=false;

    private static final int DEFAULT_BUFFER_SIZE = 6144;

    public interface UploadCallbacks {
        void onProgressUpdate(String imgName,int percentage);
        void onError();
        void onFinish();
    }


    public ViewProgress(String imgName,final File file, String content_type,  final  UploadCallbacks listener,Boolean pFlag) {
        this.content_type = content_type;
        mFile = file;
        mListener = listener;
        mImgName=imgName;
        flag=pFlag;
    }



    @Override
    public MediaType contentType() {
        return MediaType.parse(content_type+"/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;


            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    if(uploaded>0)
                    {
                        handler.post(new ProgressUpdater(uploaded, fileLength));
                    }

                    if(uploaded==fileLength) {

                    }
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            } finally {
                in.close();
            }

       flag=true;

    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate(mImgName,(int)(100 * mUploaded / mTotal));
            }
    }
}
