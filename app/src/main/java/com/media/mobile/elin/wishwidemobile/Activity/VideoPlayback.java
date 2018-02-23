/*===============================================================================
Copyright (c) 2016-2017 PTC Inc. All Rights Reserved.


Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.media.mobile.elin.wishwidemobile.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.media.mobile.elin.wishwidemobile.Control.SampleApplicationControl_Video;
import com.media.mobile.elin.wishwidemobile.Model.Beacon_Marker;
import com.media.mobile.elin.wishwidemobile.Model.Marker_data;
import com.media.mobile.elin.wishwidemobile.R;
import com.media.mobile.elin.wishwidemobile.Renderer.VideoPlaybackRenderer;
import com.media.mobile.elin.wishwidemobile.SampleAppMenu.SampleAppMenu;
import com.media.mobile.elin.wishwidemobile.SampleAppMenu.SampleAppMenuGroup;
import com.media.mobile.elin.wishwidemobile.SampleAppMenu.SampleAppMenuInterface;
import com.media.mobile.elin.wishwidemobile.Session.SampleApplicationException;
import com.media.mobile.elin.wishwidemobile.Session.SampleApplicationSession_Video;
import com.media.mobile.elin.wishwidemobile.utils.LoadingDialogHandler;
import com.media.mobile.elin.wishwidemobile.utils.SampleApplicationGLView;
import com.media.mobile.elin.wishwidemobile.utils.Texture;
import com.vuforia.*;
import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Vector;


// The AR activity for the VideoPlayback sample.
public class VideoPlayback extends AppCompatActivity implements
        SampleApplicationControl_Video, SampleAppMenuInterface {
    //hong
    //check Beacons & one beacon targeting
    public Beacon_Marker beacon_marker = null;
//    boolean isTargetHolding = false;
//    ArrayList<String> scanBeaconList = new ArrayList<>();
//    ArrayList<String> notExistBeacon = new ArrayList<>();
//    ScanResult scanResult = null;
//    CentralManager centralManager;

    private static final String LOGTAG = "VideoPlayback";

    SampleApplicationSession_Video vuforiaAppSession;

    Activity mActivity;

    // Helpers to detect events such as double tapping:
    private GestureDetector mGestureDetector = null;
    private SimpleOnGestureListener mSimpleListener = null;

    //cpyoon
    //the Target markers:
    public static final int NUM_TARGETS = 2;
    public static final int STONES = 0;
    public static final int CHIPS = 1;
    public static final String TARGETNAME[] = {"Stone", "Chips"};


    private int mSeekPosition[] = null;
    private boolean mWasPlaying[] = null;

    // A boolean to indicate whether we come from full screen:
    private boolean mReturningFromFullScreen = false;

    // Our OpenGL view:
    private SampleApplicationGLView mGlView;

    // Our renderer:
    private VideoPlaybackRenderer mRenderer;

    // The textures we will use for rendering:
    private Vector<Texture> mTextures;

    DataSet dataSetStonesAndChips = null;

    private RelativeLayout mUILayout;

    private boolean mPlayFullscreenVideo = false;

    private SampleAppMenu mSampleAppMenu;

    private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(
            this);

    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;
    private AlertDialog mARGameDialog;

    boolean mIsInitialized = false;

    private Toast mToast;

    // Called when the activity first starts or the user navigates back
    // to an activity.
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);

        //비콘 더미
        beacon_marker = new Beacon_Marker("C100470032D3");
        Marker_data marker_data = new Marker_data("stones", 2, 5);
        beacon_marker.addList(marker_data);

        vuforiaAppSession = new SampleApplicationSession_Video(this);
        mToast = Toast.makeText(this, "null", Toast.LENGTH_SHORT);
        mActivity = this;

        //프로그레스바로 로딩 애니메이션 보여줌
        startLoadingAnimation();

        //비콘 라이브러리 사용(비콘 스캔)
//        centralManager = CentralManager.getInstance();
//        centralManager.init(this);
//        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
//            @Override
//            public void onPeripheralScan(Central central, Peripheral peripheral) {
//                Log.d(LOGTAG, "주변 비콘 스캔 중");
//                Log.d(LOGTAG, "scanBeaconList 값 확인: " + scanBeaconList.toString());
//                Log.d(LOGTAG, "notExistBeacon 값 확인: " + notExistBeacon.toString());
//                if (peripheral.getDistance() < 20) {   //50cm 안에 있는지
//                    String str = peripheral.getBDAddress().replace(":", "");
//                    Log.d(LOGTAG, "맥주소 확인: " + peripheral.getBDAddress());
//
//                    if (beacon_marker == null && !isTargetHolding && !notExistBeacon.contains(str)) {
//                        Log.d(LOGTAG, "처음 실행해서 들어왔다!!" + str);
//                        //앱실행 후 처음 비콘이 스캔되었을 때, 제외 목록에 해당 비콘 맥주소가 없을 때
//                        isTargetHolding = true;
//                        new MarkerFileLoad().execute(str);
//                    }
//                    else {
//                        if(beacon_marker.m_Macaddress.equals(str)) {
//                            isTargetHolding = true;
//                        }
//                        else {
//                            if(!scanBeaconList.contains(str)) {
//                                scanBeaconList.add(str);
//                            }
//                        }
//                    }
//                }
//            }
//        });


        vuforiaAppSession
                .initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();

        // Create the gesture detector that will handle the single and
        // double taps:
        mSimpleListener = new SimpleOnGestureListener();
        mGestureDetector = new GestureDetector(getApplicationContext(),
                mSimpleListener);

        mSeekPosition = new int[NUM_TARGETS];
        mWasPlaying = new boolean[NUM_TARGETS];

        // Set the double tap listener:
        mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {
            public boolean onDoubleTap(MotionEvent e) {
                // We do not react to this event
                return false;
            }


            public boolean onDoubleTapEvent(MotionEvent e) {
                // We do not react to this event
                return false;
            }

            // Handle the single tap(한번 터치가 확실할 경우 발생)
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d(LOGTAG, "onSingleTapConfirmed()...");
                //touch event must have beacon target
                if (beacon_marker != null) {
                    Log.d(LOGTAG, "타겟 비콘이 있을 때 마커 정보에 따른 AR setting");
                    Beacon_Marker beaconobj = beacon_marker;
                    final Handler autofocusHandler = new Handler();
                    // Do not react if the StartupScreen is being displayed
                    for (int i = 0; i < beaconobj.marker_datas.size(); i++) {
                        Marker_data obj = beaconobj.marker_datas.get(i);
                        //hong
                        //marker event type 2 : screen touch event
                        Log.d(LOGTAG, "type 확인: " + obj.type);
                        if (obj.type == 2) {
                            //cpyoon
                            // Verify that the tap happened inside the object
                            if (mRenderer != null) {
                                int target = -1;
                                if (obj.str_name.equals("stones"))
                                    target = VideoPlayback.STONES;
                                else
                                    target = VideoPlayback.CHIPS;
                                int result = mRenderer.isTapOnScreenInsideTarget(target, e.getX(), e.getY());
                                Log.d("Touch Event", "touched : " + result);
                                if (result >= 0) {
//                                    mToast.setText("select target : " + TARGETNAME[target] + " objects : " + result);
//                                    mToast.show();
                                    //insert event code...
                                    showDialog();

                                    break;
                                }
                            } else {
                                Log.d(LOGTAG, "VideoPlaybackRenderer null일 때 실행");
                                boolean result = CameraDevice.getInstance().setFocusMode(
                                        CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
                                if (!result)
                                    Log.e("SingleTapConfirmed", "Unable to trigger focus");

                                // Generates a Handler to trigger continuous auto-focus
                                // after 1 second
                                autofocusHandler.postDelayed(new Runnable() {
                                    public void run() {
                                        final boolean autofocusResult = CameraDevice.getInstance().setFocusMode(
                                                CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

                                        if (!autofocusResult)
                                            Log.e("SingleTapConfirmed", "Unable to re-enable continuous auto-focus");
                                    }
                                }, 1000L);

                            }
                        }
                    }
                }

                return true;
            }
        });
    }


    // We want to load specific textures from the APK, which we will later
    // use for rendering.
    private void loadTextures() {
        //cpyoon
        //Load texture file (png, jpg, etc)
        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar1.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar2.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar3.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar4.png", getAssets()));

    }


    // Called when the activity will start interacting with the user.
    protected void onResume() {
        Log.d(LOGTAG, "onResume");
        super.onResume();
//        scanResult = new ScanResult();
//        scanResult.start();
//        centralManager.startScanning();

        showProgressIndicator(true);
        vuforiaAppSession.onResume();

        mReturningFromFullScreen = false;
    }


    // Called when returning from the full screen player
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
    }


    public void onConfigurationChanged(Configuration config) {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);

        vuforiaAppSession.onConfigurationChanged();
    }


    // Called when the system is about to start resuming a previous activity.
    protected void onPause() {
        Log.d(LOGTAG, "onPause");
        super.onPause();

//        scanResult.isCon = false;
//        centralManager.stopScanning();
        if (mGlView != null) {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }

        mReturningFromFullScreen = false;

        try {
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }
    }


    // The final call you receive before your activity is destroyed.
    protected void onDestroy() {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();


        try {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }

        // Unload texture:
        mTextures.clear();
        mTextures = null;

        System.gc();
    }


    // Pause all movies except one
    // if the value of 'except' is -1 then
    // do a blanket pause
    private void pauseAll(int except) {
    }


    // Do not exit immediately and instead show the startup screen
    public void onBackPressed() {
        pauseAll(-1);
        super.onBackPressed();
    }


    private void startLoadingAnimation() {
        Log.d(LOGTAG, "로딩바");
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay,
                null);

        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLUE);

        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUILayout
                .findViewById(R.id.loading_indicator);

        // Shows the loading indicator at start
        loadingDialogHandler
                .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

        // Adds the inflated layout to the view
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }


    // Initializes AR application components.
    private void initApplicationAR() {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();

        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        mRenderer = new VideoPlaybackRenderer(this, vuforiaAppSession);
        mRenderer.setTextures(mTextures);
        mRenderer.setmHandler(m_Handler);

        mGlView.setRenderer(mRenderer);

    }


    // We do not handle the touch event here, we just forward it to the
    // gesture detector
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (mSampleAppMenu != null)
            result = mSampleAppMenu.processEvent(event);

        // Process the Gestures
        if (!result)
            mGestureDetector.onTouchEvent(event);

        return result;
    }


    @Override
    public boolean doInitTrackers() {
        // Indicate if the trackers were initialized correctly
        boolean result = true;

        // Initialize the image tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        Tracker tracker = trackerManager.initTracker(ObjectTracker
                .getClassType());
        if (tracker == null) {
            Log.d(LOGTAG, "Failed to initialize ObjectTracker.");
            result = false;
        }

        return result;
    }


    @Override
    public boolean doLoadTrackersData() {
        // Get the image tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null) {
            Log.d(
                    LOGTAG,
                    "Failed to load tracking data set because the ObjectTracker has not been initialized.");
            return false;
        }

        // Create the data sets:
        dataSetStonesAndChips = objectTracker.createDataSet();
        if (dataSetStonesAndChips == null) {
            Log.d(LOGTAG, "Failed to create a new tracking data.");
            return false;
        }

        //cpyoon
        // Load the data sets:
        if (!dataSetStonesAndChips.load("StonesAndChips.xml", STORAGE_TYPE.STORAGE_APPRESOURCE)) {
            Log.d(LOGTAG, "Failed to load data set.");
            return false;
        }

        // Activate the data set:
        if (!objectTracker.activateDataSet(dataSetStonesAndChips)) {
            Log.d(LOGTAG, "Failed to activate data set.");
            return false;
        }

        Log.d(LOGTAG, "Successfully loaded and activated data set.");
        return true;
    }


    @Override
    public boolean doStartTrackers() {
        // Indicate if the trackers were started correctly
        boolean result = true;

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null) {
            objectTracker.start();
            Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 2);
        } else
            result = false;

        return result;
    }


    @Override
    public boolean doStopTrackers() {
        // Indicate if the trackers were stopped correctly
        boolean result = true;

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();
        else
            result = false;

        return result;
    }


    @Override
    public boolean doUnloadTrackersData() {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;

        // Get the image tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null) {
            Log.d(
                    LOGTAG,
                    "Failed to destroy the tracking data set because the ObjectTracker has not been initialized.");
            return false;
        }

        if (dataSetStonesAndChips != null) {
            if (objectTracker.getActiveDataSet(0) == dataSetStonesAndChips
                    && !objectTracker.deactivateDataSet(dataSetStonesAndChips)) {
                Log.d(
                        LOGTAG,
                        "Failed to destroy the tracking data set StonesAndChips because the data set could not be deactivated.");
                result = false;
            } else if (!objectTracker.destroyDataSet(dataSetStonesAndChips)) {
                Log.d(LOGTAG,
                        "Failed to destroy the tracking data set StonesAndChips.");
                result = false;
            }

            dataSetStonesAndChips = null;
        }

        return result;
    }


    @Override
    public boolean doDeinitTrackers() {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;

        // Deinit the image tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        trackerManager.deinitTracker(ObjectTracker.getClassType());

        return result;
    }

    @Override
    public void onInitARDone(SampleApplicationException exception) {
        if (exception == null) {
            initApplicationAR();

            mRenderer.setActive(true);

            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));

            // Sets the UILayout to be drawn in front of the camera
            mUILayout.bringToFront();

            // Hides the Loading Dialog
            loadingDialogHandler
                    .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

            // Sets the layout background to transparent
            mUILayout.setBackgroundColor(Color.TRANSPARENT);

            vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);

            mSampleAppMenu = new SampleAppMenu(this, this, "Video Playback",
                    mGlView, mUILayout, null);
            setSampleAppMenuSettings();

            mIsInitialized = true;

        } else {
            Log.e(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
    }

    @Override
    public void onVuforiaResumed() {
        if (mGlView != null) {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
    }

    @Override
    public void onVuforiaStarted() {
        mRenderer.updateConfiguration();
        // Set camera focus mode
        if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO)) {
            // If continuous autofocus mode fails, attempt to set to a different mode
            if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO)) {
                CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
            }
        }

        showProgressIndicator(false);
    }


    public void showProgressIndicator(boolean show) {
        if (loadingDialogHandler != null) {
            if (show) {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
            } else {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
            }
        }
    }


    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message) {
        final String errorMessage = message;
        runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        VideoPlayback.this);
                builder
                        .setMessage(errorMessage)
                        .setTitle(getString(R.string.INIT_ERROR))
                        .setCancelable(false)
                        .setIcon(0)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });

                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }

    // Shows initialization error messages as System dialogs
    public void showDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mARGameDialog != null) {
                    mARGameDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        VideoPlayback.this);
                builder
                        .setMessage("축하합니다.\n쿠폰을 획득하셨습니다!!")
                        .setTitle("혜택")
                        .setCancelable(false)
                        .setIcon(0)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();

                                        Intent intent = new Intent(VideoPlayback.this, MainActivity.class);
                                        intent.putExtra("loadUrl", "http://220.230.113.159:8080/");
                                        startActivity(intent);
                                    }
                                });

                mARGameDialog = builder.create();
                mARGameDialog.show();
            }
        });
    }


    @Override
    public void onVuforiaUpdate(State state) {
    }

    final private static int CMD_BACK = -1;
    final private static int CMD_FULLSCREEN_VIDEO = 1;


    // This method sets the menu's settings
    private void setSampleAppMenuSettings() {
        SampleAppMenuGroup group;

        group = mSampleAppMenu.addGroup("", false);
        group.addTextItem(getString(R.string.menu_back), -1);

        group = mSampleAppMenu.addGroup("", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            group.addSelectionItem(getString(R.string.menu_playFullscreenVideo),
                    CMD_FULLSCREEN_VIDEO, mPlayFullscreenVideo);
        }

        mSampleAppMenu.attachMenu();
    }


    @Override
    public boolean menuProcess(int command) {
        boolean result = true;

        switch (command) {
            case CMD_BACK:
                finish();
                break;

            case CMD_FULLSCREEN_VIDEO:
                break;

        }

        return result;
    }


    Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //here insert virtual touch handling
//            mToast.setText("Virtual Touch Event : " + msg.obj.toString() + ", " + msg.arg2 + " touched");
//            mToast.show();

            showDialog();
        }
    };

    //hong
    //check the target beacon
//    public class ScanResult extends Thread {
//        public boolean isCon = true;
//
//        @Override
//        public void run() {
//            while (isCon) {
//
//                try {
//                    Log.d(LOGTAG, "scanBeaconList 값 확인: " + scanBeaconList );
//                    Thread.sleep(3000);
//                    if (!isTargetHolding && scanBeaconList.size() > 0) {
//                        beacon_marker = new Beacon_Marker(scanBeaconList.get(0));
//                        Log.d(LOGTAG, "ScanResult, MarkerFileLoad 실행 직전 " + beacon_marker.m_Macaddress);
//                        new MarkerFileLoad().execute(beacon_marker.m_Macaddress);
//                    }
//                    scanBeaconList.clear();
//                    isTargetHolding = false;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public class MarkerFileLoad extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                FileInputStream fis = new FileInputStream(getFilesDir().getAbsolutePath() + params[0] + ".txt");
                InputStreamReader bis = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(bis);

                String line;
                String page = "";

                while ((line = br.readLine()) != null) {
                    page += line;
                }

//                isTargetHolding = true;
                Log.e("marker", params[0]);
                beacon_marker = new Beacon_Marker(params[0]);
                JSONArray ja = new JSONArray(page);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    Log.d(LOGTAG, "마커명: " + jo.getString("ww_marker_name"));
                    Log.d(LOGTAG, "이벤트타입: " + jo.getString("ww_marker_eventtype"));
                    Log.d(LOGTAG, "오브젝트수: " + jo.getString("ww_marker_num"));
                    Marker_data obj = new Marker_data(jo.getString("ww_marker_name"), jo.getInt("ww_marker_eventtype"), jo.getInt("ww_marker_num"));
                    beacon_marker.addList(obj);
                    Log.d(LOGTAG, "beacon_marker에 넣었는지 확인:" + beacon_marker.m_Macaddress);
                }

            } catch (FileNotFoundException e) {
                Log.e(LOGTAG, "파일 못 찾는 에러");
                e.printStackTrace();
//                notExistBeacon.add(params[0]);
//                isTargetHolding = false;
                beacon_marker = null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
