/*===============================================================================
Copyright (c) 2016-2017 PTC Inc. All Rights Reserved.


Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.media.mobile.elin.wishwidemobile.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.media.mobile.elin.wishwidemobile.FileDownloader;
import com.media.mobile.elin.wishwidemobile.FileFetcher;
import com.media.mobile.elin.wishwidemobile.Model.GameBenefitVO;
import com.media.mobile.elin.wishwidemobile.Model.GameCharacterFileVO;
import com.media.mobile.elin.wishwidemobile.Model.MarkerVO;
import com.media.mobile.elin.wishwidemobile.Model.MembershipCustomerVO;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;


// The AR activity for the VideoPlayback sample.
public class VideoPlayback extends Activity implements
        SampleApplicationControl_Video, SampleAppMenuInterface {

    private static final String LOGTAG = "VideoPlayback";

    SampleApplicationSession_Video vuforiaAppSession;

    Activity mActivity;

    // Helpers to detect events such as double tapping:
    private GestureDetector mGestureDetector = null;
    private SimpleOnGestureListener mSimpleListener = null;

    private Context mContext = this;

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

//    private SampleAppMenu mSampleAppMenu;

    private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);

    // Alert Dialog used to display SDK errors
    private AlertDialog mDialog;

    boolean mIsInitialized = false;

    public MarkerVO mMarkerVO;
    public MembershipCustomerVO membershipCustomerVO;

    FileFetcher mFileFetcher;
    private FileDownloader<GameCharacterFileVO> mFileDownloader;
    public static int mCompletedFileCnt = 0;

    private Toast mToast;

    // Called when the activity first starts or the user navigates back
    // to an activity.
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);

        //GameSetting 위한 정보 parsing
        mMarkerVO = parseJsonGameSetting(getIntent().getStringExtra("gameSetting"));
        membershipCustomerVO = parseJsonMembershipCustomer(getIntent().getStringExtra("membershipCustomerVO"));

        Handler responseHandler = new Handler();
        mFileDownloader = new FileDownloader<>(responseHandler);
        mFileDownloader.setFileDownloaderListener(new FileDownloader.FileDownloaderListener<GameCharacterFileVO>() {
            @Override
            public void onFileDownloaded() {
                if (mCompletedFileCnt == mMarkerVO.getMarkerGameCharacterCnt()) {
                    initializeAR();

                    return;
                }

                mFileDownloader.queueFile();
            }
        });
        mFileDownloader.start();
        mFileDownloader.getLooper();

        vuforiaAppSession = new SampleApplicationSession_Video(this);
        mToast = Toast.makeText(mContext, "null", Toast.LENGTH_SHORT);
        mActivity = this;

        startLoadingAnimation();

        //게임 캐릭터 파일 다운로드
        mFileFetcher = new FileFetcher();
        downloadTextures();
    }

    private void initializeAR() {
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


            // Handle the single tap
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //touch event must have beacon target
                if (mMarkerVO != null) {
                    if (mMarkerVO.getMarkerTouchEventCode().equals("R")) {
                        //cpyoon
                        // Verify that the tap happened inside the object
                        if (mRenderer != null) {
                            int target = -1;
                            if (mMarkerVO.getMarkerVuforiaCode().equals("stones"))
                                target = VideoPlayback.STONES;
                            else
                                target = VideoPlayback.CHIPS;

                            int result = mRenderer.isTapOnScreenInsideTarget(target, e.getX(), e.getY());

                            if (result >= 0) {
//                                mToast.setText("select target : " + TARGETNAME[target] + " objects : " + result);
//                                mToast.show();

                                int gameCharacterNum = mMarkerVO.getMarkerGameCharacterCnt();
                                int gameBenefitNum = 0;
                                int randomNum = new Random().nextInt(gameCharacterNum);
                                List<GameBenefitVO> gameBenefitList = new ArrayList();

                                for (int i = 0; i < mMarkerVO.getGameBenefitList().size(); i++) {
                                    Log.d(LOGTAG, "게임혜택등급" + i + ": " + mMarkerVO.getGameBenefitList().get(i).getGameBenefitGradeTypeCode());
                                    Log.d(LOGTAG, "멤버쉽등급: " + membershipCustomerVO.getMembershipCustomerGrade());
                                    if (mMarkerVO.getGameBenefitList().get(i).getGameBenefitGradeTypeCode().equals(membershipCustomerVO.getMembershipCustomerGrade())) {
                                        gameBenefitList.add(mMarkerVO.getGameBenefitList().get(i));
                                        gameBenefitNum++;
                                    }
                                }

                                Log.d(LOGTAG, "유효혜택 수: " + gameBenefitNum);
                                Log.d(LOGTAG, "선택된 값: " + randomNum);
                                Log.d(LOGTAG, "결과: " + (randomNum < gameBenefitNum));

                                if (randomNum < gameBenefitNum) {
                                    //혜택
                                    GameBenefitVO gameBenefitVO = gameBenefitList.get(randomNum);
                                    if (gameBenefitVO.getGameBenefitTypeCode().equals("P")) {
                                        showBenefitGainMessage(
                                                gameBenefitVO.getGameBenefitTypeCode(),
                                                gameBenefitVO.getGameBenefitTypeValue(),
                                                "축하합니다!\\n" + gameBenefitVO.getGameBenefitTypeValue() + "p를 획득하셨습니다.\\n내일 다시 도전해주세요.");
                                    }
                                    else {
                                        showBenefitGainMessage(
                                                gameBenefitVO.getGameBenefitTypeCode(),
                                                gameBenefitVO.getGameBenefitTypeValue(),
                                                "축하합니다!\\n" + gameBenefitVO.getGameBenefitTitle() + "를 획득하셨습니다.\\n내일 다시 도전해주세요.");
                                    }

                                }
                                else {
                                    //꽝
                                    showBenefitGainMessage("BOOM", 0,"꽝!\\n다시 도전해주세요.");
                                }

                                return true;
                            }
                        }
                    }
                }
                return true;
            }

        });

        vuforiaAppSession
                .initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void showBenefitGainMessage(final String type, final int value, final String msg) {
        //축하합니다!\nㅌ를 획득하셨습니다.\n내일 다시 도전해주세요.
        //꽝!\n다시 도전해주세요.
        runOnUiThread(new Runnable() {
            public void run() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        VideoPlayback.this);

                if (type.equals("BOOM")) {
                    builder
                            .setMessage(msg)
                            .setTitle("알림")
                            .setIcon(0)
                            .setPositiveButton("도전",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    })
                            .setNegativeButton("나가기",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            stopAR();
                                            finish();
                                        }
                                    });
                }
                else {
                    builder
                            .setMessage(msg)
                            .setTitle("알림")
                            .setIcon(0)
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent();
                                            intent.putExtra("wideManagerId", mMarkerVO.getWideManagerId());
                                            intent.putExtra("wwNo", mMarkerVO.getWwNo());
                                            intent.putExtra("markerNo", mMarkerVO.getMarkerNo());
                                            intent.putExtra("gameBenefitTypeCode", type);
                                            intent.putExtra("gameBenefitTypeValue", value);
                                            intent.putExtra("membershipCustomerNo", membershipCustomerVO.getMembershipCustomerNo());

                                            setResult(1, intent);

                                            stopAR();
                                            finish();
                                        }
                                    });
                }

                mDialog = builder.create();
                mDialog.show();
            }
        });
    }

    private MarkerVO parseJsonGameSetting(String jsonData) {
        MarkerVO markerVO = new MarkerVO();

        try {
            JSONObject gameSetting = new JSONObject(jsonData);

            markerVO.setMarkerNo(gameSetting.optInt("markerNo"));
            markerVO.setWwNo(gameSetting.optInt("wwNo"));
            markerVO.setWideManagerId(gameSetting.optString("wideManagerId"));
            markerVO.setMarkerGameCharacterCnt(gameSetting.optInt("markerGameCharacterCnt"));
            markerVO.setMarkerGameTypeCode(gameSetting.optString("markerGameTypeCode"));
            markerVO.setMarkerTouchEventCode(gameSetting.optString("markerTouchEventCode"));
            markerVO.setMarkerVuforiaCode(gameSetting.optString("markerVuforiaCode"));

            List<GameBenefitVO> gameBenefitList = new ArrayList<>();
            List<GameCharacterFileVO> gameCharacterFileList = new ArrayList<>();
            JSONArray arrGameBenefit = gameSetting.getJSONArray("gameBenefitList");
            JSONArray arrGameCharacterFile = gameSetting.getJSONArray("gameCharacterFileList");

            for (int i = 0; i < arrGameBenefit.length(); i++) {
                JSONObject objGameBenefit = arrGameBenefit.getJSONObject(i);
                GameBenefitVO gameBenefitVO = new GameBenefitVO();

                gameBenefitVO.setGameBenefitNo(objGameBenefit.getInt("gameBenefitNo"));
                gameBenefitVO.setMarkerNo(objGameBenefit.getInt("markerNo"));
                gameBenefitVO.setWwNo(objGameBenefit.getInt("wwNo"));
                gameBenefitVO.setWideManagerId(objGameBenefit.getString("wideManagerId"));
                gameBenefitVO.setMarkerGameTypeCode(objGameBenefit.getString("markerGameTypeCode"));
                gameBenefitVO.setGameBenefitGradeTypeCode(objGameBenefit.getString("gameBenefitGradeTypeCode"));
                gameBenefitVO.setGameBenefitTypeCode(objGameBenefit.getString("gameBenefitTypeCode"));
                gameBenefitVO.setGameBenefitTypeValue(objGameBenefit.getInt("gameBenefitTypeValue"));
                gameBenefitVO.setGameBenefitTitle(objGameBenefit.getString("gameBenefitTitle"));

                gameBenefitList.add(gameBenefitVO);
            }

            for (int i = 0; i < arrGameCharacterFile.length(); i++) {
                JSONObject objGameBenefit = arrGameCharacterFile.getJSONObject(i);
                GameCharacterFileVO gameCharacterFileVO = new GameCharacterFileVO();

                gameCharacterFileVO.setCharacterFileNo(objGameBenefit.getInt("characterFileNo"));
                gameCharacterFileVO.setMarkerNo(objGameBenefit.getInt("markerNo"));
                gameCharacterFileVO.setWideManagerId(objGameBenefit.getString("wideManagerId"));
                gameCharacterFileVO.setMarkerGameTypeCode(objGameBenefit.getString("markerGameTypeCode"));
                gameCharacterFileVO.setCharacterFileDataType(objGameBenefit.getString("characterFileDataType"));
                gameCharacterFileVO.setCharacterFileSeq(objGameBenefit.getInt("characterFileSeq"));
                gameCharacterFileVO.setCharacterFileSize(objGameBenefit.getInt("characterFileSize"));
                gameCharacterFileVO.setCharacterFileName(objGameBenefit.getString("characterFileName"));
                gameCharacterFileVO.setCharacterDbFile(objGameBenefit.getString("characterDbFile"));
                gameCharacterFileVO.setCharacterFileUrl(objGameBenefit.getString("characterFileUrl"));
                gameCharacterFileVO.setCharacterFileThumbnailName(objGameBenefit.getString("characterFileThumbnailName"));
                gameCharacterFileVO.setCharacterFileThumbnailUrl(objGameBenefit.getString("characterFileThumbnailUrl"));
                gameCharacterFileVO.setCharacterFileSession(objGameBenefit.getString("characterFileSession"));

                gameCharacterFileList.add(gameCharacterFileVO);
            }

            markerVO.setGameBenefitList(gameBenefitList);
            markerVO.setGameCharacterFileList(gameCharacterFileList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return markerVO;
    }

    private MembershipCustomerVO parseJsonMembershipCustomer(String jsonData) {
        MembershipCustomerVO membershipCustomerVO = new MembershipCustomerVO();

        try {
            JSONObject gameSetting = new JSONObject(jsonData);

            membershipCustomerVO.setMembershipCustomerNo(gameSetting.optInt("membershipCustomerNo"));
            membershipCustomerVO.setWideCustomerNo(gameSetting.optInt("wideCustomerNo"));
            membershipCustomerVO.setMembershipCustomerPhone(gameSetting.optString("membershipCustomerPhone"));
            membershipCustomerVO.setMembershipCustomerBenefitType(gameSetting.optString("membershipCustomerBenefitType"));
            membershipCustomerVO.setMembershipCustomerBenefitValue(gameSetting.optInt("membershipCustomerBenefitValue"));
            membershipCustomerVO.setMembershipCustomerGrade(gameSetting.optString("membershipCustomerGrade"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return membershipCustomerVO;
    }

    private void downloadTextures() {
        List<GameCharacterFileVO> gameCharacterFileList = mMarkerVO.getGameCharacterFileList();

        for (GameCharacterFileVO vo : gameCharacterFileList) {
            mFileFetcher.downloadFile(vo);
        }

        mFileDownloader.queueFile();
    }

    // We want to load specific textures from the APK, which we will later
    // use for rendering.
    private void loadTextures() {
        //cpyoon
        //Load texture file (png, jpg, etc)

        List<String> filePaths = mFileFetcher.getFilePaths();

        for (String filePath : filePaths) {

            mTextures.add(Texture.loadTextureFromInputStream(filePath));
        }

        Log.d(LOGTAG, "이미지 통합: " + mTextures);
        for (int i = 0; i < mTextures.size(); i++) {
           Log.d(LOGTAG, "이미지 정보 확인: " + mTextures.get(i)) ;
        }
    }


    // Called when the activity will start interacting with the user.
    protected void onResume() {
        Log.d(LOGTAG, "onResume");
        super.onResume();
//        scanResult = new ScanResult();
//        scanResult.start();
//        centralManager.startScanning();

        showProgressIndicator(true);
//        vuforiaAppSession.onResume();

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

        stopAR();
    }

    private void stopAR() {
        if (mGlView != null) {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }

        mReturningFromFullScreen = false;

        mFileFetcher.removeFileAll();

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

//        if (mFileDownloader != null) {
//            mFileDownloader.clearQueue();
//            mFileDownloader.quit();
//        }

        try {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }

        // Unload texture:
        if (mTextures != null) {
            mTextures.clear();
            mTextures = null;
        }

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
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay,
                null);

        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);

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
//        if (mSampleAppMenu != null)
//            result = mSampleAppMenu.processEvent(event);

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

        int numTrackables = dataSetStonesAndChips.getNumTrackables();
        Log.d(LOGTAG, "numTrackables 확인: " + numTrackables);
        for (int count = 0; count < numTrackables; count++) {
            Trackable trackable = dataSetStonesAndChips.getTrackable(count);
//            if(isExtendedTrackingActive())
//            {
//                trackable.startExtendedTracking();
//            }
            trackable.startExtendedTracking();

            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data "
                    + (String) trackable.getUserData());
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

//            mSampleAppMenu = new SampleAppMenu(this, this, "Video Playback",
//                    mGlView, mUILayout, null);
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
                if (mDialog != null) {
                    mDialog.dismiss();
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

                mDialog = builder.create();
                mDialog.show();
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
//        SampleAppMenuGroup group;
//
//        group = mSampleAppMenu.addGroup("", false);
//        group.addTextItem(getString(R.string.menu_back), -1);
//
//        group = mSampleAppMenu.addGroup("", true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            group.addSelectionItem(getString(R.string.menu_playFullscreenVideo),
//                    CMD_FULLSCREEN_VIDEO, mPlayFullscreenVideo);
//        }
//
//        mSampleAppMenu.attachMenu();
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
            mToast.setText("Virtual Touch Event : " + msg.obj.toString() + ", " + msg.arg2 + " touched");
            mToast.show();

        }
    };
}
