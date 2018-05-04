///*===============================================================================
//Copyright (c) 2016-2017 PTC Inc. All Rights Reserved.
//
//
//Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.
//
//Vuforia is a trademark of PTC Inc., registered in the United States and other
//countries.
//===============================================================================*/
//
//package com.media.mobile.elin.wishwidemobile.Activity;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.GestureDetector.OnDoubleTapListener;
//import android.view.GestureDetector.SimpleOnGestureListener;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//import com.media.mobile.elin.wishwidemobile.Control.SampleApplicationControl_Video;
//import com.media.mobile.elin.wishwidemobile.Renderer.VideoPlaybackRenderer;
//import com.media.mobile.elin.wishwidemobile.Session.SampleApplicationException;
//import com.media.mobile.elin.wishwidemobile.Session.SampleApplicationSession_Video;
//import com.media.mobile.elin.wishwidemobile.utils.LoadingDialogHandler;
//import com.media.mobile.elin.wishwidemobile.utils.SampleApplicationGLView;
//import com.media.mobile.elin.wishwidemobile.utils.Texture;
//import com.vuforia.*;
//
//import java.util.Vector;
//
//
//// The AR activity for the VideoPlayback sample.
//public class VideoPlayback2 extends Activity implements
//        SampleApplicationControl_Video
//{
//    //hong
//    //check Beacons & one beacon targeting
//    public Beacon_Marker beacon_marker = null;
//
//    private static final String LOGTAG = "VideoPlayback";
//
//    SampleApplicationSession_Video vuforiaAppSession;
//
//    Activity mActivity;
//
//    // Helpers to detect events such as double tapping:
//    private GestureDetector mGestureDetector = null;
//    private SimpleOnGestureListener mSimpleListener = null;
//
//    //cpyoon
//    //the Target markers:
//    public static final int NUM_TARGETS = 2;
//    public static final int STONES = 0;
//    public static final int CHIPS = 1;
//    public static final String TARGETNAME[] = {"Stone" , "Chips"};
//
//
//    private int mSeekPosition[] = null;
//    private boolean mWasPlaying[] = null;
//
//    // A boolean to indicate whether we come from full screen:
//    private boolean mReturningFromFullScreen = false;
//
//    // Our OpenGL view:
//    private SampleApplicationGLView mGlView;
//
//    // Our renderer:
//    private VideoPlaybackRenderer mRenderer;
//
//    // The textures we will use for rendering:
//    private Vector<Texture> mTextures;
//
//    DataSet dataSetStonesAndChips = null;
//
//    private RelativeLayout mUILayout;
//
//    private boolean mPlayFullscreenVideo = false;
//
//    private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(
//        this);
//
//    // Alert Dialog used to display SDK errors
//    private AlertDialog mErrorDialog;
//
//    boolean mIsInitialized = false;
//
//    private Toast mToast;
//    // Called when the activity first starts or the user navigates back
//    // to an activity.
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        Log.d(LOGTAG, "onCreate");
//        super.onCreate(savedInstanceState);
//
//        vuforiaAppSession = new SampleApplicationSession_Video(this);
//        mToast = Toast.makeText(this,"null",Toast.LENGTH_SHORT);
//        mActivity = this;
//
//        startLoadingAnimation();
//
//        beacon_marker = new Beacon_Marker("abcdefg3322");
//
//        //_name: 마커명
//        //_type: 1(가상버튼), 2(실제버튼)
//        //_num: 띄울 객체 수
//        Marker_data data = new Marker_data("stones", 2, 5);
//        beacon_marker.marker_datas.add(data);
//
////        centralManager = CentralManager.getInstance();
////        centralManager.init(this);
////        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
////            @Override
////            public void onPeripheralScan(Central central, Peripheral peripheral) {
////                if(peripheral.getDistance()<0.5){
////                    String str = peripheral.getBDAddress().replace(":","");
////
////                    if(beacon_marker == null && !isTargetHolding && !notExistBeacon.contains(str))
////                    {
////                        isTargetHolding=true;
////                        new MarkerFileLoad().execute(str);
////                    }
////                    if(beacon_marker.m_Macaddress.equals(str))
////                    {
////                        isTargetHolding = true;
////                    }
////                    else
////                    {
////                        if(!scanBeaconList.contains(str))
////                        {
////                            scanBeaconList.add(str);
////                        }
////                    }
////                }
////            }
////        });
//
//
//
//        vuforiaAppSession
//            .initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        // Load any sample specific textures:
//        mTextures = new Vector<Texture>();
//        loadTextures();
//
//        // Create the gesture detector that will handle the single and
//        // double taps:
//        mSimpleListener = new SimpleOnGestureListener();
//        mGestureDetector = new GestureDetector(getApplicationContext(),
//            mSimpleListener);
//
//        mSeekPosition = new int[NUM_TARGETS];
//        mWasPlaying = new boolean[NUM_TARGETS];
//
//        // Set the double tap listener:
//        mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener()
//        {
//            public boolean onDoubleTap(MotionEvent e)
//            {
//               // We do not react to this event
//               return false;
//            }
//
//
//            public boolean onDoubleTapEvent(MotionEvent e)
//            {
//                // We do not react to this event
//                return false;
//            }
//
//
//            // Handle the single tap
//            public boolean onSingleTapConfirmed(MotionEvent e)
//            {
//                //touch event must have beacon target
//                if(beacon_marker != null) {
//                    Beacon_Marker beaconobj = beacon_marker;
//                    final Handler autofocusHandler = new Handler();
//                    // Do not react if the StartupScreen is being displayed
//                    for (int i = 0; i < beaconobj.marker_datas.size(); i++) {
//                        Marker_data obj = beaconobj.marker_datas.get(i);
//                        //hong
//                        //marker event type 2 : screen touch event
//                        if (obj.type == 2) {
//                            //cpyoon
//                            // Verify that the tap happened inside the object
//                            if (mRenderer != null) {
//                                int target = -1;
//                                if (obj.str_name.equals("stones"))
//                                    target = VideoPlayback2.STONES;
//                                else
//                                    target = VideoPlayback2.CHIPS;
//                                int result = mRenderer.isTapOnScreenInsideTarget(target, e.getX(), e.getY());
//                                Log.d("Touch Event", "touched : " + result);
//                                if (result >= 0) {
//                                    mToast.setText("select target : " + TARGETNAME[target] + " objects : " + result);
//                                    mToast.show();
//                                    //insert event code...
//
//                                    break;
//                                }
//                            } else {
//                        /*
//                        boolean result = CameraDevice.getInstance().setFocusMode(
//                                CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
//                        if (!result)
//                            Log.e("SingleTapConfirmed", "Unable to trigger focus");
//
//                        // Generates a Handler to trigger continuous auto-focus
//                        // after 1 second
//                        autofocusHandler.postDelayed(new Runnable()
//                        {
//                            public void run()
//                            {
//                                final boolean autofocusResult = CameraDevice.getInstance().setFocusMode(
//                                        CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
//
//                                if (!autofocusResult)
//                                    Log.e("SingleTapConfirmed", "Unable to re-enable continuous auto-focus");
//                            }
//                        }, 1000L);
//                        */
//                            }
//                        }
//                    }
//                }
//                return true;
//            }
//        });
//    }
//
//
//    // We want to load specific textures from the APK, which we will later
//    // use for rendering.
//    private void loadTextures()
//    {
//        //cpyoon
//        //Load texture file (png, jpg, etc)
//        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar.png", getAssets()));
//        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar1.png", getAssets()));
//        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar2.png", getAssets()));
//        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar3.png", getAssets()));
//        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/testar4.png", getAssets()));
//
//    }
//
//
//    // Called when the activity will start interacting with the user.
//    protected void onResume()
//    {
//        Log.d(LOGTAG, "onResume");
//        super.onResume();
////        scanResult = new ScanResult();
////        scanResult.start();
////        centralManager.startScanning();
//
//        showProgressIndicator(true);
//        vuforiaAppSession.onResume();
//
//        mReturningFromFullScreen = false;
//    }
//
//
//    // Called when returning from the full screen player
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if (requestCode == 1)
//        {
//
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        }
//    }
//
//
//    public void onConfigurationChanged(Configuration config)
//    {
//        Log.d(LOGTAG, "onConfigurationChanged");
//        super.onConfigurationChanged(config);
//
//        vuforiaAppSession.onConfigurationChanged();
//    }
//
//
//    // Called when the system is about to start resuming a previous activity.
//    protected void onPause()
//    {
//        Log.d(LOGTAG, "onPause");
//        super.onPause();
//
////        scanResult.isCon = false;
////        centralManager.stopScanning();
//        if (mGlView != null)
//        {
//            mGlView.setVisibility(View.INVISIBLE);
//            mGlView.onPause();
//        }
//
//        mReturningFromFullScreen = false;
//
//        try
//        {
//            vuforiaAppSession.pauseAR();
//        } catch (SampleApplicationException e)
//        {
//            Log.e(LOGTAG, e.getString());
//        }
//    }
//
//
//    // The final call you receive before your activity is destroyed.
//    protected void onDestroy()
//    {
//        Log.d(LOGTAG, "onDestroy");
//        super.onDestroy();
//
//
//        try
//        {
//            vuforiaAppSession.stopAR();
//        } catch (SampleApplicationException e)
//        {
//            Log.e(LOGTAG, e.getString());
//        }
//
//        // Unload texture:
//        mTextures.clear();
//        mTextures = null;
//
//        System.gc();
//    }
//
//
//    // Pause all movies except one
//    // if the value of 'except' is -1 then
//    // do a blanket pause
//    private void pauseAll(int except)
//    {
//    }
//
//
//    // Do not exit immediately and instead show the startup screen
//    public void onBackPressed()
//    {
//        pauseAll(-1);
//        super.onBackPressed();
//    }
//
//
//    private void startLoadingAnimation()
//    {
//        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay,
//            null);
//
//        mUILayout.setVisibility(View.VISIBLE);
//        mUILayout.setBackgroundColor(Color.BLACK);
//
//        // Gets a reference to the loading dialog
//        loadingDialogHandler.mLoadingDialogContainer = mUILayout
//            .findViewById(R.id.loading_indicator);
//
//        // Shows the loading indicator at start
//        loadingDialogHandler
//            .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
//
//        // Adds the inflated layout to the view
//        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
//            LayoutParams.MATCH_PARENT));
//    }
//
//
//    // Initializes AR application components.
//    private void initApplicationAR()
//    {
//        // Create OpenGL ES view:
//        int depthSize = 16;
//        int stencilSize = 0;
//        boolean translucent = Vuforia.requiresAlpha();
//
//        mGlView = new SampleApplicationGLView(this);
//        mGlView.init(translucent, depthSize, stencilSize);
//
//        mRenderer = new VideoPlaybackRenderer(this, vuforiaAppSession);
//        mRenderer.setTextures(mTextures);
//        mRenderer.setmHandler(m_Handler);
//
//        mGlView.setRenderer(mRenderer);
//
//    }
//
//
//    // We do not handle the touch event here, we just forward it to the
//    // gesture detector
//    public boolean onTouchEvent(MotionEvent event)
//    {
//        boolean result = false;
//
//        // Process the Gestures
//        if (!result)
//            mGestureDetector.onTouchEvent(event);
//
//        return result;
//    }
//
//
//    @Override
//    public boolean doInitTrackers()
//    {
//        // Indicate if the trackers were initialized correctly
//        boolean result = true;
//
//        // Initialize the image tracker:
//        TrackerManager trackerManager = TrackerManager.getInstance();
//        Tracker tracker = trackerManager.initTracker(ObjectTracker
//            .getClassType());
//        if (tracker == null)
//        {
//            Log.d(LOGTAG, "Failed to initialize ObjectTracker.");
//            result = false;
//        }
//
//        return result;
//    }
//
//
//    @Override
//    public boolean doLoadTrackersData()
//    {
//        // Get the image tracker:
//        TrackerManager trackerManager = TrackerManager.getInstance();
//        ObjectTracker objectTracker = (ObjectTracker) trackerManager
//            .getTracker(ObjectTracker.getClassType());
//        if (objectTracker == null)
//        {
//            Log.d(
//                LOGTAG,
//                "Failed to load tracking data set because the ObjectTracker has not been initialized.");
//            return false;
//        }
//
//        // Create the data sets:
//        dataSetStonesAndChips = objectTracker.createDataSet();
//        if (dataSetStonesAndChips == null)
//        {
//            Log.d(LOGTAG, "Failed to create a new tracking data.");
//            return false;
//        }
//
//        //cpyoon
//        // Load the data sets:
//        if (!dataSetStonesAndChips.load("StonesAndChips.xml", STORAGE_TYPE.STORAGE_APPRESOURCE))
//        {
//            Log.d(LOGTAG, "Failed to load data set.");
//            return false;
//        }
//
//        // Activate the data set:
//        if (!objectTracker.activateDataSet(dataSetStonesAndChips))
//        {
//            Log.d(LOGTAG, "Failed to activate data set.");
//            return false;
//        }
//
//        int numTrackables = dataSetStonesAndChips.getNumTrackables();
////        Log.d(LOGTAG, "numTrackables 확인: " + numTrackables);
//        for (int count = 0; count < numTrackables; count++) {
//            Trackable trackable = dataSetStonesAndChips.getTrackable(count);
////            if(isExtendedTrackingActive())
////            {
////                trackable.startExtendedTracking();
////            }
//            trackable.startExtendedTracking();
//
//            String name = "Current Dataset : " + trackable.getName();
//            trackable.setUserData(name);
//            Log.d(LOGTAG, "UserData:Set the following user data "
//                    + trackable.getUserData());
//        }
//
//        Log.d(LOGTAG, "Successfully loaded and activated data set.");
//        return true;
//    }
//
//
//    @Override
//    public boolean doStartTrackers()
//    {
//        // Indicate if the trackers were started correctly
//        boolean result = true;
//
//        Tracker objectTracker = TrackerManager.getInstance().getTracker(
//            ObjectTracker.getClassType());
//        if (objectTracker != null)
//        {
//            objectTracker.start();
//            Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 2);
//        } else
//            result = false;
//
//        return result;
//    }
//
//
//    @Override
//    public boolean doStopTrackers()
//    {
//        // Indicate if the trackers were stopped correctly
//        boolean result = true;
//
//        Tracker objectTracker = TrackerManager.getInstance().getTracker(
//            ObjectTracker.getClassType());
//        if (objectTracker != null)
//            objectTracker.stop();
//        else
//            result = false;
//
//        return result;
//    }
//
//
//    @Override
//    public boolean doUnloadTrackersData()
//    {
//        // Indicate if the trackers were unloaded correctly
//        boolean result = true;
//
//        // Get the image tracker:
//        TrackerManager trackerManager = TrackerManager.getInstance();
//        ObjectTracker objectTracker = (ObjectTracker) trackerManager
//            .getTracker(ObjectTracker.getClassType());
//        if (objectTracker == null)
//        {
//            Log.d(
//                LOGTAG,
//                "Failed to destroy the tracking data set because the ObjectTracker has not been initialized.");
//            return false;
//        }
//
//        if (dataSetStonesAndChips != null)
//        {
//            if (objectTracker.getActiveDataSet(0) == dataSetStonesAndChips
//                && !objectTracker.deactivateDataSet(dataSetStonesAndChips))
//            {
//                Log.d(
//                    LOGTAG,
//                    "Failed to destroy the tracking data set StonesAndChips because the data set could not be deactivated.");
//                result = false;
//            } else if (!objectTracker.destroyDataSet(dataSetStonesAndChips))
//            {
//                Log.d(LOGTAG,
//                    "Failed to destroy the tracking data set StonesAndChips.");
//                result = false;
//            }
//
//            dataSetStonesAndChips = null;
//        }
//
//        return result;
//    }
//
//
//    @Override
//    public boolean doDeinitTrackers()
//    {
//        // Indicate if the trackers were deinitialized correctly
//        boolean result = true;
//
//        // Deinit the image tracker:
//        TrackerManager trackerManager = TrackerManager.getInstance();
//        trackerManager.deinitTracker(ObjectTracker.getClassType());
//
//        return result;
//    }
//
//
//    @Override
//    public void onInitARDone(SampleApplicationException exception)
//    {
//
//        if (exception == null)
//        {
//            initApplicationAR();
//
//            mRenderer.setActive(true);
//
//            // Now add the GL surface view. It is important
//            // that the OpenGL ES surface view gets added
//            // BEFORE the camera is started and video
//            // background is configured.
//            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT));
//
//            // Sets the UILayout to be drawn in front of the camera
//            mUILayout.bringToFront();
//
//            // Hides the Loading Dialog
//            loadingDialogHandler
//                .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
//
//            // Sets the layout background to transparent
//            mUILayout.setBackgroundColor(Color.TRANSPARENT);
//
//            vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
//
//
//            mIsInitialized = true;
//
//        } else
//        {
//            Log.e(LOGTAG, exception.getString());
//            showInitializationErrorMessage(exception.getString());
//        }
//
//    }
//
//    @Override
//    public void onVuforiaResumed()
//    {
//        if (mGlView != null)
//        {
//            mGlView.setVisibility(View.VISIBLE);
//            mGlView.onResume();
//        }
//    }
//
//    @Override
//    public void onVuforiaStarted()
//    {
//        mRenderer.updateConfiguration();
//        // Set camera focus mode
//        if(!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO))
//        {
//            // If continuous autofocus mode fails, attempt to set to a different mode
//            if(!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO))
//            {
//                CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
//            }
//        }
//
//        showProgressIndicator(false);
//    }
//
//
//    public void showProgressIndicator(boolean show)
//    {
//        if (loadingDialogHandler != null)
//        {
//            if (show)
//            {
//                loadingDialogHandler
//                        .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
//            }
//            else
//            {
//                loadingDialogHandler
//                        .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
//            }
//        }
//    }
//
//
//    // Shows initialization error messages as System dialogs
//    public void showInitializationErrorMessage(String message)
//    {
//        final String errorMessage = message;
//        runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                if (mErrorDialog != null)
//                {
//                    mErrorDialog.dismiss();
//                }
//
//                // Generates an Alert Dialog to show the error message
//                AlertDialog.Builder builder = new AlertDialog.Builder(
//                    VideoPlayback2.this);
//                builder
//                    .setMessage(errorMessage)
//                    .setTitle(getString(R.string.INIT_ERROR))
//                    .setCancelable(false)
//                    .setIcon(0)
//                    .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener()
//                        {
//                            public void onClick(DialogInterface dialog, int id)
//                            {
//                                finish();
//                            }
//                        });
//
//                mErrorDialog = builder.create();
//                mErrorDialog.show();
//            }
//        });
//    }
//
//
//    @Override
//    public void onVuforiaUpdate(State state)
//    {
//    }
//
//    final private static int CMD_BACK = -1;
//    final private static int CMD_FULLSCREEN_VIDEO = 1;
//
//
//    Handler m_Handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            //here insert virtual touch handling
//            mToast.setText("Virtual Touch Event : " + msg.obj.toString() + ", " + msg.arg2 +" touched");
//            mToast.show();
//
//        }
//    };
//    //hong
//    //check the target beacon
////    public class ScanResult extends Thread
////    {
////        public boolean isCon = true;
////        @Override
////        public void run() {
////            while(isCon) {
////                try {
////                    Thread.sleep(3000);
////                    if (!isTargetHolding && scanBeaconList.size()>0) {
////                        beacon_marker = new Beacon_Marker(scanBeaconList.get(0));
////                        new MarkerFileLoad().execute(beacon_marker.m_Macaddress);
////
////                    }
////                    scanBeaconList.clear();
////                    isTargetHolding = false;
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
////        }
////    }
//
////    public class MarkerFileLoad extends AsyncTask<String,Void,Void>
////    {
////        @Override
////        protected Void doInBackground(String... params) {
////            try {
////                FileInputStream fis = new FileInputStream(getFilesDir().getAbsolutePath()+params[0]+".txt");
////                InputStreamReader bis = new InputStreamReader(fis);
////                BufferedReader br = new BufferedReader(bis);
////
////                String line;
////                String page = "";
////
////                while ((line = br.readLine()) != null){
////                    page += line;
////                }
////                isTargetHolding=true;
////                Log.e("marker",params[0]);
////                beacon_marker = new Beacon_Marker(params[0]);
////                JSONArray ja = new JSONArray(page);
////                for (int i=0;i<ja.length();i++)
////                {
////                    JSONObject jo = ja.getJSONObject(i);
////                    Marker_data obj = new Marker_data(jo.getString("ww_marker_name"),jo.getInt("ww_marker_eventtype"),jo.getInt("ww_marker_num"));
////                    beacon_marker.addList(obj);
////                }
////
////            } catch (FileNotFoundException e) {
////                e.printStackTrace();
////                notExistBeacon.add(params[0]);
////                isTargetHolding = false;
////                beacon_marker = null;
////            } catch (IOException e) {
////                e.printStackTrace();
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////            return null;
////        }
////    }
//}
