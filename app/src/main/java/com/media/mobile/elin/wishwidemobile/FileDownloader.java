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
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap();
    private Handler mResponseHandler;
    private FileDownloaderListener<T> mFileDownloaderListener;

    private FileFetcher mFileFetcher;

    public FileDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        mFileFetcher = new FileFetcher();
    }

    public interface FileDownloaderListener<T> {
        void onFileDownloaded(T target, int responseCode);
    }

    public void setFileDownloaderListner(FileDownloaderListener<T> listener) {
        mFileDownloaderListener = listener;
    }

    public void queueFile(T target, String url) {
        if (url == null) {
            mRequestMap.remove(target);
        }
        else {
            mRequestMap.put(target, url);
            Log.d(TAG, "target: " + target);
            Log.d(TAG, "들어옴");
            mRequestHandler
                    .obtainMessage(MSG_FILE_DOWNLOAD, target)
                    .sendToTarget();

        }
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
                    T target = (T) msg.obj;
                    Log.d(TAG,"들어옴2");
                    handleRequest(target);
                }
            }
        };
    }

    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);
        GameCharacterFileVO gameCharacterFileVO = (GameCharacterFileVO) target;

        final int responseCode;
        if (url == null) return;

        mFileFetcher.downloadFile(gameCharacterFileVO);

        responseCode = 1;

//        if (mFileFetcher.checkCompleteFile(gameCharacterFileVO.getCharacterFileName(), gameCharacterFileVO.getCharacterFileSize())) {
//            responseCode = 1;
//        }
//        else {
//            responseCode = 0;
//        }

        mResponseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRequestMap.get(target) != url) {
                    return;
                }

                mRequestMap.remove(target);
                mFileDownloaderListener.onFileDownloaded(target, responseCode);
            }
        }, 30000);
    }
}
