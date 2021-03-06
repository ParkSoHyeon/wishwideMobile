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
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.os.*;
//import android.util.Log;
//import android.view.*;
//import android.view.GestureDetector.OnDoubleTapListener;
//import android.view.GestureDetector.SimpleOnGestureListener;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.*;
//import com.media.mobile.elin.wishwidemobile.Control.SampleApplicationControl_Video;
//import com.media.mobile.elin.wishwidemobile.FileDownloader;
//import com.media.mobile.elin.wishwidemobile.FileFetcher;
//import com.media.mobile.elin.wishwidemobile.Model.*;
//import com.media.mobile.elin.wishwidemobile.R;
//import com.media.mobile.elin.wishwidemobile.Renderer.Game1Renderer;
//import com.media.mobile.elin.wishwidemobile.Session.SampleApplicationException;
//import com.media.mobile.elin.wishwidemobile.Session.SampleApplicationSession_Video;
//import com.media.mobile.elin.wishwidemobile.utils.LoadingDialogHandler;
//import com.media.mobile.elin.wishwidemobile.utils.SampleApplicationGLView;
//import com.media.mobile.elin.wishwidemobile.utils.Texture;
//import com.vuforia.*;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.Vector;
//
//
//// The AR activity for the VideoPlayback sample.
//public class Game1 extends Activity implements
//        SampleApplicationControl_Video {
//
//    private static final String LOGTAG = "Game1";
//    private final static String strAppDatasetPath = Environment.getExternalStorageDirectory().getPath() + "/Wishwide/game/dataset/";
//
//    SampleApplicationSession_Video vuforiaAppSession;
//
//    Activity mActivity;
//
//    private View mGame1ContentView;
//    private FrameLayout mFlEffectView;
//    private ImageView mImgEffectView;
//    private TextView mTvGame1Guide;
//
//    // Helpers to detect events such as double tapping:
//    private GestureDetector mGestureDetector = null;
//    private SimpleOnGestureListener mSimpleListener = null;
//
//    private Context mContext = this;
//
//    //cpyoon
//    //the Target markers:
//    public static final int NUM_TARGETS = 2;
//    //    public static final int STONES = 0;
//    public static final int CHIPS = 1;
//    public static final String TARGETNAME[] = {"Stone", "Chips"};
//
//    // A boolean to indicate whether we come from full screen:
//    private boolean mReturningFromFullScreen = false;
//
//    // Our OpenGL view:
//    private SampleApplicationGLView mGlView;
//
//    // Our renderer:
//    private Game1Renderer mRenderer;
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
////    private SampleAppMenu mSampleAppMenu;
//
//    private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
//
//    // Alert Dialog used to display SDK errors
//    private AlertDialog mDialog;
//    private TextView mTvReceivedBenefitGuide;
//    private Button mBtnCase1, mBtnCase2;
//
//    boolean mIsInitialized = false;
//
//    public GameSettingVO mGameSettingVO;
//    public MembershipCustomerVO mMembershipCustomerVO;
//
//    FileFetcher mFileFetcher;
//    private FileDownloader<GameCharacterFileVO> mFileDownloader;
//    public static int mCompletedCharacterFileCnt = 0;
//    public static int mCompletedDataSetFileCnt = 0;
//
//    private Toast mToast;
//
//    // Called when the activity first starts or the user navigates back
//    // to an activity.
//    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(LOGTAG, "onCreate");
//        super.onCreate(savedInstanceState);
//
//        //GameSetting 위한 정보 parsing
//        mGameSettingVO = parseJsonGameSetting(getIntent().getStringExtra("gameSetting"));
//        mMembershipCustomerVO = parseJsonMembershipCustomer(getIntent().getStringExtra("membershipCustomerVO"));
//
//        Handler responseHandler = new Handler();
//        mFileDownloader = new FileDownloader<>(responseHandler);
//        mFileDownloader.setFileDownloaderListener(new FileDownloader.FileDownloaderListener<GameCharacterFileVO>() {
//            @Override
//            public void onFileDownloaded() {
//                if ((mCompletedCharacterFileCnt == mGameSettingVO.getTotalCharacterCnt()) && (mCompletedDataSetFileCnt == 2)) {
//                    initializeAR();
//
//                    mCompletedCharacterFileCnt = 0;
//                    mCompletedDataSetFileCnt = 0;
//
//                    mFileDownloader.clearQueue();
//
//                    return;
//                }
//
//                mFileDownloader.queueFile();
//            }
//        });
//        mFileDownloader.start();
//        mFileDownloader.getLooper();
//
//        vuforiaAppSession = new SampleApplicationSession_Video(this);
////        mToast = Toast.makeText(mContext, "null", Toast.LENGTH_SHORT);
//        mActivity = this;
//
//
//        // Create the gesture detector that will handle the single and
//        // double taps:
//        mSimpleListener = new SimpleOnGestureListener();
//        mGestureDetector = new GestureDetector(getApplicationContext(),
//                mSimpleListener);
//
//
//        startLoadingAnimation();
//
//
//        mFileFetcher = new FileFetcher();
//        //dat 파일 다운로드
//        mFileFetcher.downloadDataSetFile(
//                mGameSettingVO.getMarkerDatFileUrl(),
//                mGameSettingVO.getMarkerDatFileName(),
//                mGameSettingVO.getMarkerDatFileSize(),
//                mGameSettingVO.getMarkerGameTypeCode());
//
//        //xml 파일 다운로드
//        mFileFetcher.downloadDataSetFile(
//                mGameSettingVO.getMarkerXmlFileUrl(),
//                mGameSettingVO.getMarkerXmlFileName(),
//                mGameSettingVO.getMarkerXmlFileSize(),
//                mGameSettingVO.getMarkerGameTypeCode());
//        //게임 캐릭터 파일 다운로드
//        downloadTextures();
//    }
//
//    private void initializeAR() {
//        // Load any sample specific textures:
//        mTextures = new Vector<Texture>();
//        loadTextures();
//
//
//        // Set the double tap listener:
//        mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {
//            public boolean onDoubleTap(MotionEvent e) {
//                // We do not react to this event
//                return false;
//            }
//
//
//            public boolean onDoubleTapEvent(MotionEvent e) {
//                // We do not react to this event
//                return false;
//            }
//
//
//            // Handle the single tap
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                //touch event must have beacon target
//                if (mGameSettingVO != null) {
//                    //cpyoon
//                    // Verify that the tap happened inside the object
//                    if (mRenderer != null) {
//                        int result = mRenderer.isTapOnScreenInsideTarget(e.getX(), e.getY());
//                        Log.d(LOGTAG, "뭐 누름? " + result);
//
//                        if (result >= 0) {
////                                mToast.setText("select target : " + TARGETNAME[target] + " objects : " + result);
////                                mToast.show();
//
//                            int gameCharacterNum = mGameSettingVO.getTotalCharacterCnt();
//                            int gameBenefitNum = mGameSettingVO.getBenefitCnt();
//                            int randomNum = new Random().nextInt(gameCharacterNum);
//                            List<GameBenefitVO> gameBenefitList = mGameSettingVO.getGameBenefitList();
//
//                            Log.d(LOGTAG, "유효혜택 수: " + gameBenefitNum);
//                            Log.d(LOGTAG, "선택된 값: " + randomNum);
//                            Log.d(LOGTAG, "결과: " + (randomNum < gameBenefitNum));
//
////                            if (randomNum < gameBenefitNum) {
////                                //혜택
////                                GameBenefitVO gameBenefitVO = gameBenefitList.get(randomNum);
////                                if (gameBenefitVO.getCouponDiscountTypeCode().equals("P")) {
////                                    showBenefitGainMessage(
////                                            gameBenefitVO.getCouponDiscountTypeCode(),
////                                            "축하합니다!\n" + gameBenefitVO.getCouponDiscountValue() + "p를 획득하셨습니다.\n내일 다시 도전해주세요.",
////                                            String.valueOf(gameBenefitVO.getCouponNo()),
////                                            String.valueOf(gameBenefitVO.getCouponDiscountValue()));
////                                } else if (gameBenefitVO.getCouponDiscountTypeCode().equals("S")) {
////                                    showBenefitGainMessage(
////                                            gameBenefitVO.getCouponDiscountTypeCode(),
////                                            "축하합니다!\n도장 " + gameBenefitVO.getCouponDiscountValue() + "개를 획득하셨습니다.\n내일 다시 도전해주세요.",
////                                            String.valueOf(gameBenefitVO.getCouponNo()),
////                                            String.valueOf(gameBenefitVO.getCouponDiscountValue()));
////                                } else {
////                                    String msg = "";
////                                    if (gameBenefitVO.getCouponDiscountTypeCode().equals("DCP")) {
////                                        msg = "축하합니다!\n" + gameBenefitVO.getProductTitle() + " " + gameBenefitVO.getCouponDiscountValue() + "원 할인쿠폰을 획득하셨습니다.\n내일 다시 도전해주세요.";
////                                    } else if (gameBenefitVO.getCouponDiscountTypeCode().equals("DCR")) {
////                                        msg = "축하합니다!\n" + gameBenefitVO.getProductTitle() + " " + gameBenefitVO.getCouponDiscountValue() + "% 할인쿠폰을 획득하셨습니다.\n내일 다시 도전해주세요.";
////                                    }
////                                    showBenefitGainMessage(
////                                            gameBenefitVO.getCouponDiscountTypeCode(),
////                                            msg,
////                                            String.valueOf(gameBenefitVO.getCouponNo()));
////                                }
////
////                            } else {
////                                //꽝
////                                showBenefitGainMessage("BOOM", "꽝!\n다시 도전해주세요.");
////                            }
//
//                            return true;
//                        }
//                    }
//                }
//                return true;
//            }
//
//        });
//
//        ViewGroup.LayoutParams layoutParamsControl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        LayoutInflater inflater = getLayoutInflater();
//        mGame1ContentView = inflater.inflate(R.layout.content_game1, null);
//        mGame1ContentView.setVisibility(View.GONE);
//
//        addContentView(mGame1ContentView, layoutParamsControl);
//
//        mTvGame1Guide = (TextView) mGame1ContentView.findViewById(R.id.tv_game1_guide);
//        mFlEffectView = (FrameLayout) mGame1ContentView.findViewById(R.id.fl_effect_view);
//        mImgEffectView = (ImageView) mGame1ContentView.findViewById(R.id.img_effect);
//
//        vuforiaAppSession
//                .initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//    }
//
//
//    private void showBenefitGainMessage(final String type, final String msg, final String... couponInfo) {
//        //축하합니다!\nㅌ를 획득하셨습니다.\n내일 다시 도전해주세요.
//        //꽝!\n다시 도전해주세요.
//        runOnUiThread(new Runnable() {
//            public void run() {
//                if (mDialog != null) {
//                    mDialog.dismiss();
//                }
//
//                View dialogView = LayoutInflater.from(Game1.this)
//                        .inflate(R.layout.dialog_confirm, null);
//
//                mTvReceivedBenefitGuide = (TextView) dialogView.findViewById(R.id.tv_received_benefit_guide);
//                mBtnCase1 = (Button) dialogView.findViewById(R.id.btn_case_1);
//                mBtnCase2 = (Button) dialogView.findViewById(R.id.btn_case_2);
//
//
//                // Generates an Alert Dialog to show the error message
//                AlertDialog.Builder builder = new AlertDialog.Builder(
//                        Game1.this);
//
//
//                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                        if (keyCode == KeyEvent.KEYCODE_BACK) {
//                            //Back 버튼 동작 막기
//                            return true;
//                        }
//
//                        return false;
//                    }
//                });
//
//
//                mTvReceivedBenefitGuide.setText(msg);
//
//
//                if (type.equals("BOOM")) {
//                    //꽝 이미지 표시
//                    mImgEffectView.setBackgroundResource(R.drawable.sample_bomb);
//                    mFlEffectView.setVisibility(View.VISIBLE);
//
//                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mFlEffectView.setVisibility(View.GONE);
//                        }
//                    }, 300);
//
//                    mBtnCase1.setText("재도전");
//                    mBtnCase1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mDialog.dismiss();
//                            Game1Renderer.randNum = new Random().nextInt(3);
//                        }
//                    });
//
//
//                    mBtnCase2.setText("나가기");
//                    mBtnCase2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            setResult(2, new Intent());
//
//                            finish();
//                        }
//                    });
//                } else {
//                    //서버에 보낼 쿠폰, 멤버쉽 정보 intent에 put
//                    final Intent intent = new Intent();
//                    intent.putExtra("wideManagerId", mGameSettingVO.getWideManagerId());
//                    Log.d(LOGTAG, "멤버쉽 정보 확인: " + mMembershipCustomerVO.toString());
//                    intent.putExtra("membershipCustomerNo", mMembershipCustomerVO.getMembershipCustomerNo());
//                    intent.putExtra("couponDiscountTypeCode", type);
//                    intent.putExtra("couponNo", Integer.parseInt(couponInfo[0]));
//                    if (couponInfo.length == 2) {
//                        intent.putExtra("couponDiscountTypeValue", Integer.parseInt(couponInfo[1]));
//                    } else {
//                        intent.putExtra("couponDiscountTypeValue", 0);
//                    }
//
//                    if (type.equals("S")) {
//                        intent.putExtra("movePage", "listStampAndPoint");
//                        mBtnCase1.setText("도장 보러가기");
//                    } else if (type.equals("P")) {
//                        intent.putExtra("movePage", "listStampAndPoint");
//                        mBtnCase1.setText("포인트 보러가기");
//                    } else {
//                        intent.putExtra("movePage", "listCoupon");
//                        mBtnCase1.setText("쿠폰 보러가기");
//                    }
//
//
//                    mBtnCase1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            setResult(1, intent);
//
//                            finish();
//                        }
//                    });
//
//
//                    mBtnCase2.setText("나가기");
//                    mBtnCase1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            intent.putExtra("movePage", "detailStore");
//                            setResult(1, intent);
//
//                            finish();
//                        }
//                    });
//
//                }
//
//
//                mDialog = builder
//                        .setView(dialogView)
//                        .create();
//                mDialog.setCanceledOnTouchOutside(false);
//                mDialog.show();
//            }
//        });
//    }
//
//
//    public void showGame1Guide(final String msg) {
//        final Context context = this;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // if scard is already visible with same VuMark, do nothing
//                if ((mGame1ContentView.getVisibility() == View.VISIBLE) && (mTvGame1Guide.getText().equals(msg))) {
//                    return;
//                }
////                Animation bottomDown = AnimationUtils.loadAnimation(context,
////                        R.anim.bottom_down);
//
//
//                mTvGame1Guide.setText(msg);
//
//                mGame1ContentView.bringToFront();
//                mGame1ContentView.setVisibility(View.VISIBLE);
////                mGame1ContentView.startAnimation(bottomDown);
//                // mUILayout.invalidate();
//            }
//        });
//    }
//
//
//    public void hideGame1Guide() {
//        final Context context = this;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // if card not visible, do nothing
//                if (mGame1ContentView.getVisibility() != View.VISIBLE) {
//                    return;
//                }
//                mTvGame1Guide.setText("");
//
////                Animation bottomUp = AnimationUtils.loadAnimation(context,
////                        R.anim.bottom_up);
//
////                mGame1ContentView.startAnimation(bottomUp);
//                mGame1ContentView.setVisibility(View.INVISIBLE);
//                // mUILayout.invalidate();
//            }
//        });
//    }
//
//    private GameSettingVO parseJsonGameSetting(String jsonData) {
//        GameSettingVO gameSettingVO = new GameSettingVO();
//
//        try {
//            JSONObject gameSetting = new JSONObject(jsonData);
//
//            gameSettingVO.setArGameNo(gameSetting.optInt("arGameNo"));
//            gameSettingVO.setMarkerCnt(gameSetting.optInt("markerCnt"));
//            gameSettingVO.setBenefitCnt(gameSetting.optInt("benefitCnt"));
//            gameSettingVO.setTotalCharacterCnt(gameSetting.optInt("totalCharacterCnt"));
//            gameSettingVO.setWideManagerId(gameSetting.optString("wideManagerId"));
//            gameSettingVO.setMarkerName(gameSetting.optString("markerName"));
//            gameSettingVO.setMarkerGameTypeCode(gameSetting.optString("markerGameTypeCode"));
//            gameSettingVO.setMarkerGameValue(gameSetting.optString("markerGameValue"));
//            gameSettingVO.setWideManagerId(gameSetting.optString("wideManagerId"));
//            gameSettingVO.setMarkerDatDbFile(gameSetting.optString("markerDatDbFile"));
//            gameSettingVO.setMarkerDatFileDataType(gameSetting.optString("markerDatFileDataType"));
//            gameSettingVO.setMarkerDatFileName(gameSetting.optString("markerDatFileName"));
//            gameSettingVO.setMarkerDatFileSize(gameSetting.optInt("markerDatFileSize"));
//            gameSettingVO.setMarkerDatFileUrl(gameSetting.optString("markerDatFileUrl"));
//            gameSettingVO.setMarkerXmlDbFile(gameSetting.optString("markerXmlDbFile"));
//            gameSettingVO.setMarkerXmlFileDataType(gameSetting.optString("markerXmlFileDataType"));
//            gameSettingVO.setMarkerXmlFileName(gameSetting.optString("markerXmlFileName"));
//            gameSettingVO.setMarkerXmlFileSize(gameSetting.optInt("markerXmlFileSize"));
//            gameSettingVO.setMarkerXmlFileUrl(gameSetting.optString("markerXmlFileUrl"));
//
//            List<GameBenefitVO> gameBenefitList = new ArrayList<>();
//            List<GameCharacterFileVO> gameCharacterFileList = new ArrayList<>();
//            List<MarkerVO> markerList = new ArrayList<>();
//            JSONArray arrGameBenefit = gameSetting.getJSONArray("gameBenefitList");
//            JSONArray arrGameCharacterFile = gameSetting.getJSONArray("gameCharacterFileList");
//            JSONArray arrMarker = gameSetting.getJSONArray("markerList");
//
//            for (int i = 0; i < arrMarker.length(); i++) {
//                JSONObject objGameBenefit = arrMarker.getJSONObject(i);
//                MarkerVO markerVO = new MarkerVO();
//
//                markerVO.setMarkerId(objGameBenefit.getString("markerId"));
//
//                markerList.add(markerVO);
//            }
//
//            for (int i = 0; i < arrGameBenefit.length(); i++) {
//                JSONObject objGameBenefit = arrGameBenefit.getJSONObject(i);
//                GameBenefitVO gameBenefitVO = new GameBenefitVO();
//
//                gameBenefitVO.setCouponNo(objGameBenefit.getInt("couponNo"));
//                gameBenefitVO.setCouponDeadLine(objGameBenefit.getInt("couponDeadLine"));
//                gameBenefitVO.setCouponMemberSendTypeCode(objGameBenefit.optString("couponMemberSendTypeCode"));
//                gameBenefitVO.setWideManagerId(objGameBenefit.getString("wideManagerId"));
//                gameBenefitVO.setCouponTitle(objGameBenefit.getString("couponTitle"));
//                gameBenefitVO.setCouponDiscountTypeCode(objGameBenefit.getString("couponDiscountTypeCode"));
//                gameBenefitVO.setCouponDiscountValue(objGameBenefit.optInt("couponDiscountValue"));
//                gameBenefitVO.setCouponPublishedCode(objGameBenefit.getInt("couponPublishedCode"));
//                gameBenefitVO.setProductTitle(objGameBenefit.getString("productTitle"));
//
//                gameBenefitList.add(gameBenefitVO);
//            }
//
//            for (int i = 0; i < arrGameCharacterFile.length(); i++) {
//                JSONObject objGameBenefit = arrGameCharacterFile.getJSONObject(i);
//                GameCharacterFileVO gameCharacterFileVO = new GameCharacterFileVO();
//
//                gameCharacterFileVO.setCharacterFileNo(objGameBenefit.getInt("characterFileNo"));
//                gameCharacterFileVO.setMarkerGameTypeCode(objGameBenefit.getString("markerGameTypeCode"));
//                gameCharacterFileVO.setCharacterFileDataType(objGameBenefit.getString("characterFileDataType"));
//                gameCharacterFileVO.setCharacterFileSeq(objGameBenefit.getInt("characterFileSeq"));
//                gameCharacterFileVO.setCharacterFileSize(objGameBenefit.getInt("characterFileSize"));
//                gameCharacterFileVO.setCharacterFileName(objGameBenefit.getString("characterFileName"));
//                gameCharacterFileVO.setCharacterDbFile(objGameBenefit.getString("characterDbFile"));
//                gameCharacterFileVO.setCharacterFileUrl(objGameBenefit.getString("characterFileUrl"));
//                gameCharacterFileVO.setCharacterFileValue(objGameBenefit.getString("characterFileValue"));
//
//                gameCharacterFileList.add(gameCharacterFileVO);
//            }
//
//            gameSettingVO.setGameBenefitList(gameBenefitList);
//            gameSettingVO.setGameCharacterFileList(gameCharacterFileList);
//            gameSettingVO.setMarkerList(markerList);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return gameSettingVO;
//    }
//
//
//    private MembershipCustomerVO parseJsonMembershipCustomer(String jsonData) {
//        MembershipCustomerVO membershipCustomerVO = new MembershipCustomerVO();
//
//        try {
//            JSONObject gameSetting = new JSONObject(jsonData);
//
//            membershipCustomerVO.setMembershipCustomerNo(gameSetting.optInt("membershipCustomerNo"));
//            membershipCustomerVO.setWideCustomerNo(gameSetting.optInt("wideCustomerNo"));
//            membershipCustomerVO.setMembershipCustomerPhone(gameSetting.optString("membershipCustomerPhone"));
//            membershipCustomerVO.setMembershipCustomerBenefitType(gameSetting.optString("membershipCustomerBenefitType"));
//            membershipCustomerVO.setMembershipCustomerBenefitValue(gameSetting.optInt("membershipCustomerBenefitValue"));
//            membershipCustomerVO.setMembershipCustomerGrade(gameSetting.optString("membershipCustomerGrade"));
//            membershipCustomerVO.setNewMembershipCustomerCode(gameSetting.optInt("newMembershipCustomerCode"));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return membershipCustomerVO;
//    }
//
//
//    private void downloadTextures() {
//        List<GameCharacterFileVO> gameCharacterFileList = mGameSettingVO.getGameCharacterFileList();
//
//        for (GameCharacterFileVO vo : gameCharacterFileList) {
//            mFileFetcher.downloadGameCharacterFile(vo, mGameSettingVO.getMarkerGameTypeCode());
//        }
//
//        mFileDownloader.queueFile();
//    }
//
//    // We want to load specific textures from the APK, which we will later
//    // use for rendering.
//    private void loadTextures() {
//        //cpyoon
//        //Load texture file (png, jpg, etc)
//
//        List<String> filePaths = mFileFetcher.getGameCharacterFilePaths();
//
//        for (String filePath : filePaths) {
//            Log.d(LOGTAG, "filePath 정보 확인: " + filePaths);
//        }
//
//        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/gift.png", getAssets()));
//
//        for (String filePath : filePaths) {
//
//            mTextures.add(Texture.loadTextureFromInputStream(filePath));
//        }
//
//        Log.d(LOGTAG, "이미지 통합: " + mTextures);
//        for (int i = 0; i < mTextures.size(); i++) {
//            Log.d(LOGTAG, "이미지 정보 확인: " + mTextures.get(i));
//        }
//    }
//
//
//    // Called when the activity will start interacting with the user.
//    protected void onResume() {
//        Log.d(LOGTAG, "onResume");
//        super.onResume();
////        scanResult = new ScanResult();
////        scanResult.start();
////        centralManager.startScanning();
//
//        showProgressIndicator(true);
////        vuforiaAppSession.onResume();
//
//        mReturningFromFullScreen = false;
//    }
//
//
//    // Called when returning from the full screen player
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 1) {
//
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        }
//    }
//
//
//    public void onConfigurationChanged(Configuration config) {
//        Log.d(LOGTAG, "onConfigurationChanged");
//        super.onConfigurationChanged(config);
//
//        vuforiaAppSession.onConfigurationChanged();
//    }
//
//
//    // Called when the system is about to start resuming a previous activity.
//    protected void onPause() {
//        Log.d(LOGTAG, "onPause");
//        super.onPause();
//
////        scanResult.isCon = false;
////        centralManager.stopScanning();
//
//        stopAR();
//    }
//
//
//    private void stopAR() {
//        if (mGlView != null) {
//            mGlView.setVisibility(View.INVISIBLE);
//            mGlView.onPause();
//        }
//
//        mReturningFromFullScreen = false;
//
//        mFileFetcher.removeFileAll();
//
//        vuforiaAppSession.pauseAR();
//    }
//
//
//    // The final call you receive before your activity is destroyed.
//    protected void onDestroy() {
//        Log.d(LOGTAG, "onDestroy");
//        super.onDestroy();
//
////        if (mFileDownloader != null) {
////            mFileDownloader.clearQueue();
////            mFileDownloader.quit();
////        }
//
//        if (mGlView != null) {
//            mGlView = null;
//        }
//
//        if (mRenderer != null) {
//            mRenderer = null;
//        }
//
//        try {
//            vuforiaAppSession.stopAR();
//        } catch (SampleApplicationException e) {
//            Log.e(LOGTAG, e.getString());
//        }
//
//        // Unload texture:
//        if (mTextures != null) {
//            mTextures.clear();
//            mTextures = null;
//        }
//
//        mFileFetcher.removeFileAll();
//
//        System.gc();
//    }
//
//
//    // Pause all movies except one
//    // if the value of 'except' is -1 then
//    // do a blanket pause
//    private void pauseAll(int except) {
//    }
//
//
//    // Do not exit immediately and instead show the startup screen
//    public void onBackPressed() {
//        pauseAll(-1);
//        super.onBackPressed();
//    }
//
//
//    private void startLoadingAnimation() {
//        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay,
//                null);
//
//        mUILayout.setVisibility(View.VISIBLE);
//        mUILayout.setBackgroundColor(Color.BLACK);
//
//        // Gets a reference to the loading dialog
//        loadingDialogHandler.mLoadingDialogContainer = mUILayout
//                .findViewById(R.id.loading_indicator);
//
//        // Shows the loading indicator at start
//        loadingDialogHandler
//                .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
//
//        // Adds the inflated layout to the view
//        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT));
//    }
//
//
//    // Initializes AR application components.
//    private void initApplicationAR() {
//        // Create OpenGL ES view:
//        int depthSize = 16;
//        int stencilSize = 0;
//        boolean translucent = Vuforia.requiresAlpha();
//
//        mGlView = new SampleApplicationGLView(this);
//        mGlView.init(translucent, depthSize, stencilSize);
//
//        mRenderer = new Game1Renderer(this, vuforiaAppSession);
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
//    public boolean onTouchEvent(MotionEvent event) {
//        boolean result = false;
////        if (mSampleAppMenu != null)
////            result = mSampleAppMenu.processEvent(event);
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
//    public boolean doInitTrackers() {
//        // Indicate if the trackers were initialized correctly
//        boolean result = true;
//
//        // Initialize the image tracker:
//        TrackerManager trackerManager = TrackerManager.getInstance();
//        Tracker tracker = trackerManager.initTracker(ObjectTracker
//                .getClassType());
//        if (tracker == null) {
//            Log.d(LOGTAG, "Failed to initialize ObjectTracker.");
//            result = false;
//        }
//
//        return result;
//    }
//
//
//    @Override
//    public boolean doLoadTrackersData() {
//        Log.d(LOGTAG, "doLoadTrackersData()..." + strAppDatasetPath + mGameSettingVO.getMarkerXmlFileName());
//        // Get the image tracker:
//        TrackerManager trackerManager = TrackerManager.getInstance();
//        ObjectTracker objectTracker = (ObjectTracker) trackerManager
//                .getTracker(ObjectTracker.getClassType());
//        if (objectTracker == null) {
//            Log.d(
//                    LOGTAG,
//                    "Failed to load tracking data set because the ObjectTracker has not been initialized.");
//            return false;
//        }
//
//        // Create the data sets:
//        dataSetStonesAndChips = objectTracker.createDataSet();
//        if (dataSetStonesAndChips == null) {
//            Log.d(LOGTAG, "Failed to create a new tracking data.");
//            return false;
//        }
//
//        //cpyoon
//        // Load the data sets:
//        //디바이스 SD 카드에 저장되어 있는 DataSet 파일 로드
////        if (!dataSetStonesAndChips.load(strAppDatasetPath + mGameSettingVO.getMarkerXmlFileName(), STORAGE_TYPE.STORAGE_ABSOLUTE)) {
////            Log.d(LOGTAG, "Failed to load data set.");
////            return false;
////        }
//        //assets 파일에 저장되어 있는 DataSet 파일 로드
//        if (!dataSetStonesAndChips.load("StonesAndChips.xml", STORAGE_TYPE.STORAGE_APPRESOURCE)) {
//            Log.d(LOGTAG, "Failed to load data set.");
//            return false;
//        }
//
//        // Activate the data set:
//        if (!objectTracker.activateDataSet(dataSetStonesAndChips)) {
//            Log.d(LOGTAG, "Failed to activate data set.");
//            return false;
//        }
//
//        int numTrackables = dataSetStonesAndChips.getNumTrackables();
//        Log.d(LOGTAG, "numTrackables 확인: " + numTrackables);
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
//    public boolean doStartTrackers() {
//        // Indicate if the trackers were started correctly
//        boolean result = true;
//
//        Tracker objectTracker = TrackerManager.getInstance().getTracker(
//                ObjectTracker.getClassType());
//
//        if (objectTracker != null) {
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
//    public boolean doStopTrackers() {
//        // Indicate if the trackers were stopped correctly
//        boolean result = true;
//
//        Tracker objectTracker = TrackerManager.getInstance().getTracker(
//                ObjectTracker.getClassType());
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
//    public boolean doUnloadTrackersData() {
//        // Indicate if the trackers were unloaded correctly
//        boolean result = true;
//
//        // Get the image tracker:
//        TrackerManager trackerManager = TrackerManager.getInstance();
//        ObjectTracker objectTracker = (ObjectTracker) trackerManager
//                .getTracker(ObjectTracker.getClassType());
//        if (objectTracker == null) {
//            Log.d(
//                    LOGTAG,
//                    "Failed to destroy the tracking data set because the ObjectTracker has not been initialized.");
//            return false;
//        }
//
//        if (dataSetStonesAndChips != null) {
//            if (objectTracker.getActiveDataSet(0) == dataSetStonesAndChips
//                    && !objectTracker.deactivateDataSet(dataSetStonesAndChips)) {
//                Log.d(
//                        LOGTAG,
//                        "Failed to destroy the tracking data set StonesAndChips because the data set could not be deactivated.");
//                result = false;
//            } else if (!objectTracker.destroyDataSet(dataSetStonesAndChips)) {
//                Log.d(LOGTAG,
//                        "Failed to destroy the tracking data set StonesAndChips.");
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
//    public boolean doDeinitTrackers() {
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
//    public void onInitARDone(SampleApplicationException exception) {
//
//        if (exception == null) {
//            initApplicationAR();
//
//            mRenderer.setActive(true);
//
//            // Now add the GL surface view. It is important
//            // that the OpenGL ES surface view gets added
//            // BEFORE the camera is started and video
//            // background is configured.
//            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
//                    LayoutParams.MATCH_PARENT));
//
//            // Sets the UILayout to be drawn in front of the camera
//            mUILayout.bringToFront();
//
//            // Hides the Loading Dialog
//            loadingDialogHandler
//                    .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
//
//            // Sets the layout background to transparent
//            mUILayout.setBackgroundColor(Color.TRANSPARENT);
//
//            vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
//
//
//            mIsInitialized = true;
//
//        } else {
//            Log.e(LOGTAG, exception.getString());
//            showInitializationErrorMessage(exception.getString());
//        }
//
//    }
//
//    @Override
//    public void onVuforiaResumed() {
//        if (mGlView != null) {
//            mGlView.setVisibility(View.VISIBLE);
//            mGlView.onResume();
//        }
//    }
//
//    @Override
//    public void onVuforiaStarted() {
//        mRenderer.updateConfiguration();
//        // Set camera focus mode
//        if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO)) {
//            // If continuous autofocus mode fails, attempt to set to a different mode
//            if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO)) {
//                CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
//            }
//        }
//
//        showProgressIndicator(false);
//    }
//
//
//    public void showProgressIndicator(boolean show) {
//        if (loadingDialogHandler != null) {
//            if (show) {
//                loadingDialogHandler
//                        .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
//            } else {
//                loadingDialogHandler
//                        .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
//            }
//        }
//    }
//
//
//    // Shows initialization error messages as System dialogs
//    public void showInitializationErrorMessage(String message) {
//        final String errorMessage = message;
//        runOnUiThread(new Runnable() {
//            public void run() {
//                if (mDialog != null) {
//                    mDialog.dismiss();
//                }
//
//                // Generates an Alert Dialog to show the error message
//                AlertDialog.Builder builder = new AlertDialog.Builder(
//                        Game1.this);
//                builder
//                        .setMessage(errorMessage)
//                        .setTitle(getString(R.string.INIT_ERROR))
//                        .setCancelable(false)
//                        .setIcon(0)
//                        .setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        finish();
//                                    }
//                                });
//
//                mDialog = builder.create();
//                mDialog.show();
//            }
//        });
//    }
//
//
//    @Override
//    public void onVuforiaUpdate(State state) {
//    }
//
//    final private static int CMD_BACK = -1;
//    final private static int CMD_FULLSCREEN_VIDEO = 1;
//
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (mDialog != null) {
//                mDialog.dismiss();
//            }
//
//            View dialogView = LayoutInflater.from(Game1.this)
//                    .inflate(R.layout.dialog_confirm, null);
//
//            mTvReceivedBenefitGuide = (TextView) dialogView.findViewById(R.id.tv_received_benefit_guide);
//            mBtnCase1 = (Button) dialogView.findViewById(R.id.btn_case_1);
//            mBtnCase2 = (Button) dialogView.findViewById(R.id.btn_case_2);
//
//
//            // Generates an Alert Dialog to show the error message
//            AlertDialog.Builder builder = new AlertDialog.Builder(
//                    Game1.this);
//
//            mTvReceivedBenefitGuide.setText("게임을 종료하시겠습니까?");
//
//            mBtnCase1.setText("예");
//            mBtnCase1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//
//
//            mBtnCase2.setText("아니요");
//            mBtnCase2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mDialog.dismiss();
//                }
//            });
//
//            //다이아로그박스 출력
//            mDialog = builder
//                    .setView(dialogView)
//                    .create();
//            mDialog.setCanceledOnTouchOutside(false);
//            mDialog.show();
//
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//
//    Handler m_Handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            //here insert virtual touch handling
//            mToast.setText("Virtual Touch Event : " + msg.obj.toString() + ", " + msg.arg2 + " touched");
//            mToast.show();
//
//        }
//    };
//}
