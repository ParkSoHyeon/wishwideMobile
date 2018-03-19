package com.media.mobile.elin.wishwidemobile;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import com.media.mobile.elin.wishwidemobile.Model.GameCharacterFileVO;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileDownloader<T> extends HandlerThread {
    private static final String TAG = "FileDownloader";

    private static final int MSG_FILE_DOWNLOAD = 1;
    private static final int MSG_FILE_COMPLETED = 2;
    private static final int MSG_FILE_WAITED = 3;
    private static final int MSG_FILE_FAILED = 4;

    private Handler mRequestHandler;
//    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap();
    private Handler mResponseHandler;
    private FileDownloaderListener<T> mFileDownloaderListener;

    private FileFetcher mFileFetcher;

    public FileDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        mFileFetcher = new FileFetcher();
    }

    public interface FileDownloaderListener<T> {
        void onFileDownloaded();
    }

    public void setFileDownloaderListener(FileDownloaderListener<T> listener) {
        mFileDownloaderListener = listener;
    }

    public void queueFile() {
        Log.d(TAG, "들어옴");
        mRequestHandler
                .obtainMessage(MSG_FILE_DOWNLOAD)
                .sendToTarget();

    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MSG_FILE_DOWNLOAD);
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_FILE_DOWNLOAD) {

                    mResponseHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mFileDownloaderListener.onFileDownloaded();
                        }
                    });
                }
            }
        };
    }

    private void handleRequest(final T target) {

    }
}
