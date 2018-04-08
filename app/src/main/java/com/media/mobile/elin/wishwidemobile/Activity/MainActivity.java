package com.media.mobile.elin.wishwidemobile.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.*;
import android.widget.*;
import com.media.mobile.elin.wishwidemobile.*;
import com.media.mobile.elin.wishwidemobile.Model.Beacon_Marker;
import com.media.mobile.elin.wishwidemobile.Model.WideCustomerVO;
import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WebUrlConstance,
        SharedPreferencesConstant,
        PermissionConstant,
        Button.OnClickListener {
    private static final String TAG = "MainActivity";

    private final Context mContext = this;

    private SharedPreferences mSharedPreferences;
    private WideCustomerVO wideCustomerVO;

    //TopBar 관련 멤버변수
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    //    private TabLayout mTabLayout;
    private TextView mTvProfile;
    private Button mBtn1, mBtn2, mBtn3, mBtn4, mBtn5;
    private LinearLayout mLlTabs;

    //AR 게임 관련 멤버 변수
    FloatingActionButton mARFloatingActionButton;

    //WebView 관련 멤버변수
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefresh;
    private WebAndAppBridge mWebAndAppBridge;

    //위치서비스 관련 멤버변수
    private LocationManager mLocationManager;
    boolean mIsGPSEnabled, mIsNetworkEnabled;
    private LocationListener mLocationListener;

    //beacon list
    public Beacon_Marker beacon_marker = null;
    private ArrayList<String> scanBeaconList = new ArrayList<>();
    private ArrayList<Double> scanBeaconDistance = new ArrayList<>();
    private ArrayList<String> notExistBeacon = new ArrayList<>();
    private ArrayList<String> scanBeaconAll = new ArrayList<>();
    private BeaconScanResult beaconScanResult = null;
    private CentralManager centralManager;
    public final Map scanBeaconInfo = new HashMap();

    private AppCompatDialog progressDialog;


    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View 초기화
        initializeView();


        //메뉴탭 Listener
//        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                Log.d(TAG, "onTabSelected()..." + tab.getText());
//
//                switch (tab.getText().toString()) {
//                    case "내주변":
//                        requestLocationUpdate();
//                        break;
//                    case "방문한 매장":
//                        mWebView.loadUrl(DOMAIN_NAME + VISITED_STORE_LIST_PATH);
//                        break;
//                    case "선물가게":
//                        mWebView.loadUrl(DOMAIN_NAME + GIFT_STORE_LIST_PATH);
//                        break;
//                    case "도장/포인트":
//                        mWebView.loadUrl(DOMAIN_NAME + STAMP_AND_POINT_LIST_PATH);
//                        break;
//                    case "쿠폰":
//                        mWebView.loadUrl(DOMAIN_NAME + COUPON_LIST_PATH);
//                        break;
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                Log.d(TAG, "onTabUnselected()..." + tab.getText());
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                Log.d(TAG, "onTabReselected()...");
//            }
//        });


        //AR 게임 실행 버튼 Listener


        //AR 게임 실행


        mARFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> deniedPermissions = getDeniedPermissions(Manifest.permission.CAMERA);

                if (deniedPermissions.size() > 0) {
                    requestPermission(deniedPermissions.toArray(new String[deniedPermissions.size()]), GAME_START_PERMISSION);
                } else {
                    mWebView.loadUrl("javascript:callGameSetting()");
                }
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });


        //위치 Listener
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(TAG, "위치 확인:" + location.getLatitude() + ", " + location.getLongitude());

                mWebView.loadUrl(DOMAIN_NAME + HOME_PATH + "?lat=" + location.getLatitude() + "&lng=" + location.getLongitude());
                progressOFF();
                requestRemoveUpdate();

//                new NearbyBeaconListTask().execute(location.getLatitude(),location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        //비콘 스캔 설정 및 Listener
        centralManager = CentralManager.getInstance();
        centralManager.init(this);
        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, Peripheral peripheral) {
                final String str = peripheral.getBDAddress().replace(":", "");
                Log.d(TAG, "비콘 스캔" + str);
//                if (!scanBeaconAll.contains(str))
//                    scanBeaconAll.add(str);
//                if (scanBeaconList.contains(str)) {
//                    scanBeaconDistance.set(scanBeaconList.indexOf(str), peripheral.getDistance());
//                }

                Set key = scanBeaconInfo.keySet();

                for (Iterator iterator = key.iterator(); iterator.hasNext(); ) {
                    String keyName = (String) iterator.next();
                    String valueName = (String) scanBeaconInfo.get(keyName);

                    if (keyName.trim().equals(str.trim())) {
                        Log.d(TAG, "DB 비콘과 일치" + valueName);
                        stopBeaconScan();

                        mWebView.loadUrl(DOMAIN_NAME + STORE_DETAIL_PATH + "?wideManagerId=" + valueName);

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.CustomTheme);
//                                builder.setTitle("안내");
//                                builder.setMessage(scanBeaconInfo.get(str) + " 매장 상세로 이동하시겠습니까?");
//                                builder.setPositiveButton("이동", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                });
//                                builder.setNegativeButton("머물기", null);
//                                builder.show();
//                            }
//                        });

                    }

                    System.out.println(keyName + " = " + valueName);
                }


            }
        });


        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("안내")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(true)
                        .create()
                        .show();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("안내")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .create()
                        .show();

                return true;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted()..." + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished()..." + url);
                super.onPageFinished(view, url);

                mSwipeRefresh.setRefreshing(false);

                mARFloatingActionButton.setVisibility(View.GONE);
//                mTabLayout.setVisibility(View.VISIBLE);
                mLlTabs.setVisibility(View.VISIBLE);
                mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);


                switch (url) {
                    case DOMAIN_NAME:   //로그인
                        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);    //menu(navigation) gone setting
                        mLlTabs.setVisibility(View.GONE);

                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                        String localPhoneNum = telephonyManager.getLine1Number();
                        if (localPhoneNum != null) {
                            localPhoneNum = localPhoneNum.replace("+82", "0");
                            Log.d(TAG, "현재 디바이스의 전화번호 확인: " + localPhoneNum);

                            mWebView.loadUrl("javascript:getDevicePhoneNum(" + localPhoneNum + ")");
                        }

                        break;
                    case JOIN_PATH:
                        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);    //menu(navigation) gone setting
                        mLlTabs.setVisibility(View.GONE);
                        break;
                    case DOMAIN_NAME + VISITED_STORE_LIST_PATH: //방문한 매장
                        mBtn2.setTextColor(Color.BLUE);
                        break;
                    case DOMAIN_NAME + STORE_DETAIL_PATH:   //매장상세
                        break;
                    case DOMAIN_NAME + RECEIVED_GIFT_LIST_PATH:
                    case DOMAIN_NAME + SEND_GIFT_LIST_PATH:
                        mBtn1.setTextColor(Color.BLACK);
                        mBtn2.setTextColor(Color.BLACK);
                        mBtn3.setTextColor(Color.BLACK);
                        mBtn4.setTextColor(Color.BLACK);
                        mBtn5.setTextColor(Color.BLACK);
                        break;
                    case COUPON_LIST_PATH:
                        mBtn1.setTextColor(Color.BLACK);
                        mBtn2.setTextColor(Color.BLACK);
                        mBtn3.setTextColor(Color.BLACK);
                        mBtn4.setTextColor(Color.BLACK);
                        mBtn5.setTextColor(Color.BLUE);
                        break;
                    case STAMP_AND_POINT_LIST_PATH:
                        mBtn1.setTextColor(Color.BLACK);
                        mBtn2.setTextColor(Color.BLACK);
                        mBtn3.setTextColor(Color.BLACK);
                        mBtn4.setTextColor(Color.BLUE);
                        mBtn5.setTextColor(Color.BLACK);
                    default:
                        break;
                }

                //AR 게임 실행 버튼 visible
                if (url.contains(DOMAIN_NAME + STORE_DETAIL_PATH)) {
                    mWebView.loadUrl("javascript:callIsExecuteGame()");
                    if (!mWebAndAppBridge.ismIsExecuteGame()) {
                        Log.d(TAG, "게임이용가능");
                        mARFloatingActionButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        Log.d(TAG, "게임이용불가");
                        mARFloatingActionButton.setVisibility(View.GONE);
                    }
                } else if (url.contains(DOMAIN_NAME + HOME_PATH)) {
                    mWebView.clearHistory();

                    //권한 안내 띄우기
                    if (!mSharedPreferences.getBoolean(WHETHER_PERMISSION_GUIDE_SHOW_KEY, false)) {
                        Log.d(TAG, "권한 안내 띄우기 시도");
                        startActivityForResult(new Intent(MainActivity.this, PermissionGuideActivity.class), 00);
                    }

                }

                progressOFF();
            }

        });


        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });


        String responseCode = getIntent().getStringExtra("responseCode");
        mSharedPreferences = this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);


        if (responseCode.equals("AUTO")) {
            JSONObject objRoot = new JSONObject();
            try {
                Log.d(TAG, "전화번호 확인: " + getIntent().getStringExtra("wideCustomerPhone"));
                objRoot.put("wideCustomerPhone", getIntent().getStringExtra("wideCustomerPhone"));

                mWebView.postUrl(DOMAIN_NAME + AUTO_LOGIN_PATH, EncodingUtils.getBytes(objRoot.toString(), "UTF-8"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (responseCode.equals("LOGIN")) {
            //로그인 url 이동
            mWebView.loadUrl(DOMAIN_NAME);
        }
    }


    //View 초기화
    private void initializeView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);    //menu(navigation) visible/gone setting

//        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        mLlTabs = (LinearLayout) findViewById(R.id.ll_tabs);

        mBtn1 = (Button) findViewById(R.id.btn_1);
        mBtn2 = (Button) findViewById(R.id.btn_2);
        mBtn3 = (Button) findViewById(R.id.btn_3);
        mBtn4 = (Button) findViewById(R.id.btn_4);
        mBtn5 = (Button) findViewById(R.id.btn_5);

        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
        mBtn4.setOnClickListener(this);
        mBtn5.setOnClickListener(this);

        mBtn1.setTextColor(Color.BLUE);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mTvProfile = (TextView) findViewById(R.id.tv_profile);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_web_loading);
        mProgressBar.setMax(100);

        mARFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        mWebView = (WebView) findViewById(R.id.web_view);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebAndAppBridge = new WebAndAppBridge(mWebView);
        mWebView.addJavascriptInterface(mWebAndAppBridge, "WebAndAppBridge");
    }


    @Override
    public void onClick(View v) {
        mBtn1.setTextColor(Color.BLACK);
        mBtn2.setTextColor(Color.BLACK);
        mBtn3.setTextColor(Color.BLACK);
        mBtn4.setTextColor(Color.BLACK);
        mBtn5.setTextColor(Color.BLACK);

        switch (v.getId()) {
            case R.id.btn_1:
                mBtn1.setTextColor(Color.BLUE);
                requestLocationUpdate();
                break;
            case R.id.btn_2:
                mBtn2.setTextColor(Color.BLUE);
                mWebView.loadUrl(DOMAIN_NAME + VISITED_STORE_LIST_PATH);
                break;
            case R.id.btn_3:
                mBtn3.setTextColor(Color.BLUE);
                mWebView.loadUrl(DOMAIN_NAME + GIFT_STORE_LIST_PATH);
                break;
            case R.id.btn_4:
                mBtn4.setTextColor(Color.BLUE);
                mWebView.loadUrl(DOMAIN_NAME + STAMP_AND_POINT_LIST_PATH);
                break;
            case R.id.btn_5:
                mBtn5.setTextColor(Color.BLUE);
                mWebView.loadUrl(DOMAIN_NAME + COUPON_LIST_PATH);
                break;
        }
        progressON(this, "로딩 중...");
    }


    //Web의 javascript와 앱을 연결해주는 클래스
    private class WebAndAppBridge {
        private static final String TAG = "WebAndAppBridge";

        public static final String REQUEST_EVENT = "request";
        public static final String PERMISSION_DENIED_EVENT = "denied";
        public static final String PERMISSION_GRANTED_EVENT = "granted";

        private final WebView mWebView;
//        private int mGiftProductNo;

        private double mLatitude;
        private double mLongitude;

        private boolean mIsExecuteGame;

        public WebAndAppBridge(WebView webView) {
            mWebView = webView;
        }


        @JavascriptInterface
        public void callStore(String tel) {
            startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + tel)));
        }


        @JavascriptInterface
        public void callGameSetting(String managerId) {
            new GameSettingTask(managerId).execute();
        }

        @JavascriptInterface
        public void callIsExecuteGame(String isExecuteGame) {
            mIsExecuteGame = isExecuteGame.equals("0") ? false : true;
        }


        @JavascriptInterface
        public void getAndroidContactList(String event) {
//            Log.d(TAG, "연락처 가져오기");

            JSONObject objRoot = new JSONObject();

            try {
                switch (event) {
                    case REQUEST_EVENT:
                        List<String> deniedPermissions = getDeniedPermissions(Manifest.permission.READ_CONTACTS);

//                        mGiftProductNo = giftProductNo;

                        if (deniedPermissions.size() > 0) {
                            requestPermission(deniedPermissions.toArray(new String[deniedPermissions.size()]), CONTACT_PERMISSION);
                        } else {
                            mWebView.post(new Runnable() {
                                @Override
                                public void run() {
                                    getAndroidContactList(PERMISSION_GRANTED_EVENT);
                                }
                            });

                        }

                        objRoot.put("responseCode", "HOLD");
                        break;
                    case PERMISSION_GRANTED_EVENT:
                        objRoot.put("contacts", getContactAll());
//                        objRoot.put("giftProductNo", mGiftProductNo);
                        objRoot.put("responseCode", "SUCCESS");

                        Log.d(TAG, "byte: " + EncodingUtils.getBytes(objRoot.toString(), "UTF-8"));
                        Log.d(TAG, "string: " + objRoot.toString());

                        mWebView.postUrl(DOMAIN_NAME + CONTACT_LIST_PATH, EncodingUtils.getBytes(objRoot.toString(), "UTF-8"));
                        break;
                    case PERMISSION_DENIED_EVENT:
                        objRoot.put("responseCode", "DENIED");
//                        objRoot.put("giftProductNo", mGiftProductNo);

                        mWebView.postUrl(DOMAIN_NAME + CONTACT_LIST_PATH, EncodingUtils.getBytes(objRoot.toString(), "UTF-8"));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @JavascriptInterface
        public void requestCurrentLocation(String event) {
            switch (event) {
                case REQUEST_EVENT:
                    requestLocationUpdate();
                    break;
            }
        }

        private JSONArray getContactAll() {
            Cursor cursor = null;

            JSONArray arrContacts = new JSONArray();

            try {
                cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                int contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);

                cursor.moveToFirst();

                do {
                    JSONObject objContact = new JSONObject();

                    String idContact = cursor.getString(contactIdIdx);
                    String name = cursor.getString(nameIdx);
                    String phoneNumber = cursor.getString(phoneNumberIdx);

//                    Log.d(TAG, idContact);
//                    Log.d(TAG, name);
//                    Log.d(TAG, phoneNumber);

                    if (Pattern.matches("^(010)(\\d{3,4})(\\d{4})", phoneNumber)) {
                        objContact.put("contactId", idContact);
                        objContact.put("contactName", name);
                        objContact.put("contactPhone", phoneNumber);

                        arrContacts.put(objContact);
                    }


                }
                while (cursor.moveToNext());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return arrContacts;
        }


        @JavascriptInterface
        public void setWideCustomer(String data) {
            try {
                JSONObject objRoot = new JSONObject(data);

                if (wideCustomerVO == null) {
                    wideCustomerVO = new WideCustomerVO();
                }

                wideCustomerVO.setWideCustomerNo(objRoot.optInt("wideCustomerNo"));
                wideCustomerVO.setWideCustomerPhone(String.valueOf("0" + objRoot.optInt("wideCustomerPhone")));
                wideCustomerVO.setWideCustomerBirth(objRoot.optString("wideCustomerBirth"));
                wideCustomerVO.setWideCustomerSex(objRoot.optInt("wideCustomerSex"));
                wideCustomerVO.setWideCustomerEmail(objRoot.optString("wideCustomerEmail"));
                wideCustomerVO.setWideCustomerName(objRoot.optString("wideCustomerName"));

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(WIDE_CUSTOMER_PHONE_KEY, String.valueOf("0" + objRoot.optInt("wideCustomerPhone")));
                editor.putInt(WIDE_CUSTOMER_NO_KEY, objRoot.optInt("wideCustomerNo"));
                editor.putString(WIDE_CUSTOMER_BIRTH_KEY, objRoot.optString("wideCustomerBirth"));
                editor.putString(WIDE_CUSTOMER_SEX_KEY, objRoot.optString("wideCustomerSex"));
                editor.putString(WIDE_CUSTOMER_EMAIL_KEY, objRoot.optString("wideCustomerEmail"));
                editor.putString(WIDE_CUSTOMER_NAME_KEY, objRoot.optString("wideCustomerName"));
                editor.commit();

                mTvProfile.setText(wideCustomerVO.getWideCustomerName());

                Log.d(TAG, "고객 정보 확인: " + wideCustomerVO.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void setLatitude(double latitude) {
            mLatitude = latitude;
        }

        public void setLongitude(double longitude) {
            mLongitude = longitude;
        }

        public boolean ismIsExecuteGame() {
            return mIsExecuteGame;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (mWebView.canGoBack())) {
            mWebView.goBack();

//            WebBackForwardList list = mWebView.copyBackForwardList();

            if (mWebView.getUrl().contains(DOMAIN_NAME + GIFT_ORDER_PATH)) {
                mWebView.goBackOrForward(-1);
                // history 삭제
                mWebView.clearHistory();
            }

            return true;
        } else {
            //다이아로그박스 출력
            new AlertDialog.Builder(this)
                    .setTitle("프로그램 종료")
                    .setMessage("프로그램을 종료하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton("아니오", null).show();
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        super.onBackPressed();
    }


    //왼쪽 네비게이션바 아이템 선택 리스너
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        TabLayout.Tab tab;
        progressON(this, "로딩 중...");
        switch (id) {
            case R.id.nav_visited_store:    //방문한매장
                mBtn2.performClick();
//                tab = mTabLayout.getTabAt(1);
//                tab.select();
                break;
            case R.id.nav_nearby_stores:    //홈
                mBtn1.performClick();
//                tab = mTabLayout.getTabAt(0);
//                tab.select();
                break;
            case R.id.nav_gift_store:   //선물가게
                mBtn3.performClick();
//                tab = mTabLayout.getTabAt(2);
//                tab.select();
                break;
            case R.id.nav_received_gift:    //받은선물내역(선물함)
                mBtn1.setTextColor(Color.BLACK);
                mBtn2.setTextColor(Color.BLACK);
                mBtn3.setTextColor(Color.BLACK);
                mBtn4.setTextColor(Color.BLACK);
                mBtn5.setTextColor(Color.BLACK);
                mWebView.loadUrl(DOMAIN_NAME + RECEIVED_GIFT_LIST_PATH);
                break;
            case R.id.nav_send_gift:    //보낸선물내역
                mBtn1.setTextColor(Color.BLACK);
                mBtn2.setTextColor(Color.BLACK);
                mBtn3.setTextColor(Color.BLACK);
                mBtn4.setTextColor(Color.BLACK);
                mBtn5.setTextColor(Color.BLACK);
                mWebView.loadUrl(DOMAIN_NAME + SEND_GIFT_LIST_PATH);
                break;
            case R.id.nav_coupon:   //쿠폰함내역(쿠폰함)
                mBtn5.performClick();
//                tab = mTabLayout.getTabAt(3);
//                tab.select();
                break;
            case R.id.nav_point_and_stamp:  //도장/포인트내역
                mBtn4.performClick();
//                tab = mTabLayout.getTabAt(4);
//                tab.select();
                break;
            case R.id.nav_setting:  //환경설정
                //setting activity 이동
                startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), 11);
                break;
            default:
                progressOFF();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();

//        stopBeaconScan();
    }


    //위치 서비스 요청
    private void requestLocationUpdate() {
        List<String> deniedPermissions = getDeniedPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (deniedPermissions.size() > 0) {
            //위치 권한 있음
            requestPermission(deniedPermissions.toArray(new String[deniedPermissions.size()]), ROCATION_FIND_PERMISSION);
        } else {
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            }

            mIsGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!mIsGPSEnabled) {
                new AlertDialog.Builder(mContext)
                        .setTitle("안내")
                        .setMessage("현재 매장 위치를 더욱 쉽게 찾기 위해 위치 서비스를 켜주세요.")
                        .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("다음에", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWebView.loadUrl(DOMAIN_NAME + HOME_PATH + "?lat=0&lng=0");
                            }
                        }).show();
            }

            progressON(this, "로딩 중...");
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation == null) {
                Log.d(TAG, "최초 위치 정보 가져옴, 위치 정보 update 필요");
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            } else {
                boolean isOlderLocation = (System.currentTimeMillis() - lastKnownLocation.getTime()) > (1000 * 15);   //15초 지남

                Log.d(TAG, isOlderLocation + ", 이전 location: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude());

                if (isOlderLocation) {
                    Log.d(TAG, "15초 지남, 위치 정보 update 필요");
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                } else {
                    Log.d(TAG, "최신 위치 정보임을 확인");
                    mWebView.loadUrl(DOMAIN_NAME + HOME_PATH +
                            "?lat=" + lastKnownLocation.getLatitude() + "&lng=" + lastKnownLocation.getLongitude());
                }
            }

        }
    }


    //위치 서비스 종료
    private void requestRemoveUpdate() {
        mLocationManager.removeUpdates(mLocationListener);
    }


    //권한 허용 안 된 리스트 가져오기
    private List<String> getDeniedPermissions(String... permissions) {
        List<String> deniedPermissions = new ArrayList<>();

        for (String permission : permissions) {
            boolean isDeniedPermission = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, permission + "의 권한 허용 여부: " + isDeniedPermission);

            if (!isDeniedPermission) {
                //권한 없음
                deniedPermissions.add(permission);
            }
        }

        return deniedPermissions;
    }


    //권한 요청
    private void requestPermission(String[] deniedPermissions, int requestCode) {
        Log.d(TAG, "권한 요청: " + deniedPermissions.length);
        ActivityCompat.requestPermissions(
                this,
                deniedPermissions,
                requestCode);
    }


    //권한 허용 메시지
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION:
                //저장소
                break;
            case ROCATION_FIND_PERMISSION:
                //위치 찾기
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
//                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, mLocationListener);
                } else {
                    mWebView.loadUrl(DOMAIN_NAME + HOME_PATH + "?lat=0&lng=0");
                }
                break;
            case NEARBY_BEACON_FIND_PERMISSION:
                //위치 찾기 + 주변 비콘 찾기
                break;
            case CONTACT_PERMISSION:
                //전화부
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mWebAndAppBridge.getAndroidContactList(WebAndAppBridge.PERMISSION_GRANTED_EVENT);
                } else {
                    mWebAndAppBridge.getAndroidContactList(WebAndAppBridge.PERMISSION_DENIED_EVENT);
                }
                break;
            case CALL_PERMISSION:
                //전화

                break;
            case GAME_START_PERMISSION:
                //카메라, 저장소
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mWebView.loadUrl("javascript:callGameSetting()");
                }
                break;
            default:
                break;
        }
    }


    public class BeaconFileRead extends AsyncTask<Void, Void, String> {
        private static final String TAG = "BeaconFileRead";

        @Override
        protected String doInBackground(Void... params) {

            try {
                File file = new File(getFilesDir().getAbsolutePath() + "/Beacon.txt");
                FileReader fr = null;
                int data;
                fr = new FileReader(file);


                String str = "";
                while ((data = fr.read()) != -1) {
                    str += (char) data;
                }
                fr.close();

                JSONObject objRoot = new JSONObject(str);


                JSONArray ja = objRoot.optJSONArray("nearbyBeaconVO");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    scanBeaconList.add(jo.getString("beaconMacAddress"));
                    scanBeaconInfo.put(jo.getString("beaconMacAddress"), jo.getString("wideManagerId"));
                    Log.d(TAG, "Beacon.txt 파일 읽음: " + jo.getString("beaconMacAddress"));
                    Log.d(TAG, "Beacon.txt 파일 읽음: " + scanBeaconInfo.containsKey(jo.getString("beaconMacAddress")));
                    scanBeaconDistance.add(999.0);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            startBeaconScan();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public class NearbyBeaconListTask extends AsyncTask<Double, Void, String> {
        private static final String TAG = "NearbyBeaconListTask";


        @Override
        protected String doInBackground(Double... params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();

            Log.d(TAG, "lat: " + params[0]);
            Log.d(TAG, "lng: " + params[1]);
            sbParams.append("lat").append("=").append(params[0]).append("&");
            sbParams.append("lng").append("=").append(params[1]);

            try {
                URL url = new URL(DOMAIN_NAME + NEARBY_BEACON_LIST_PATH);
                urlConn = (HttpURLConnection) url.openConnection();

                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Accept-Charset", "UTF-8");

                String strParams = sbParams.toString();
                OutputStream os = urlConn.getOutputStream();
                os.write(strParams.getBytes("UTF-8"));
                os.flush();
                os.close();
                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return null;

                // [2-4]. 읽어온 결과물 리턴.
                // 요청한 URL의 출력물을 BufferedReader로 받는다.
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                // 출력물의 라인과 그 합에 대한 변수.
                String line;
                String page = "";

                // 라인을 받아와 합친다.
                while ((line = reader.readLine()) != null) {
                    page += line;
                }

                return page;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Log.d(TAG, "주변 비콘 목록 확인: " + s);
                try {

                    FileOutputStream fos = new FileOutputStream(getFilesDir().getAbsolutePath() + "/Beacon.txt");
                    fos.write(s.getBytes());
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                new BeaconFileRead().execute();
            }
        }
    }


    //비콘 스캔 시작
    private void startBeaconScan() {
//        beaconScanResult = new BeaconScanResult();
//        beaconScanResult.start();
        centralManager.startScanning();
    }


    //비콘 스캔 종료 및 자원 반납
    private void stopBeaconScan() {
//        beaconScanResult.isCon = false;
//        beaconScanResult = null;
        Log.d(TAG, "비콘 스캔 종료");
        centralManager.stopScanning();
    }


    public class BeaconScanResult extends Thread {
        private static final String TAG = "BeaconScanResult";
        public boolean isCon = true;

        @Override
        public void run() {
            if (scanBeaconDistance.size() < 1) {
                Log.d(TAG, "주변에 비콘 없음");
                return;
            }
            while (isCon) {
                try {
                    Thread.sleep(3000);

                    double min = scanBeaconDistance.get(0);
//                    Log.d(TAG, "최소 거리 값 확인: " + min);
                    int cnt = 0;
                    for (int i = 1; i < scanBeaconList.size(); i++) {
                        double dis = scanBeaconDistance.get(i);
                        if (dis < min) {
                            min = dis;
                            cnt = i;
                        }
                    }

                    if (min != 999.0) {
                        if (beacon_marker == null) {
                            Log.d("tScanResult begin", scanBeaconList.get(cnt));
                            beacon_marker = new Beacon_Marker(scanBeaconList.get(cnt));
//                            new MarkerFileLoad().execute(beacon_marker.m_Macaddress);

                        } else if (!beacon_marker.m_Macaddress.equals(scanBeaconList.get(cnt))) {
                            Log.d("tScanResult ", scanBeaconList.get(cnt));
                            beacon_marker = new Beacon_Marker(scanBeaconList.get(cnt));
//                            new MarkerFileLoad().execute(beacon_marker.m_Macaddress);
                        }

                        stopBeaconScan();

                        //scanBeaconList.get(cnt)에 맞는 매장을 찾아 상세 화면 띄우기
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        JSONObject objRoot = new JSONObject();

        switch (requestCode) {
            case 00:
                if (resultCode == 1) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(WHETHER_PERMISSION_GUIDE_SHOW_KEY, true);
                    editor.commit();
                }
                break;
            case 11:
                if (resultCode == 1) {
                    //로그아웃
                    Log.d(TAG, "로그아웃 시도");
                    mWebView.loadUrl(DOMAIN_NAME);
                    mWebView.clearHistory();
                } else if (resultCode == 2) {
                    //설정 변경
                    Log.d(TAG, "설정 변경");
                    progressOFF();

                }
                break;
            case 7:
                Log.d(TAG, "게임1 종료");


                try {
                    if (resultCode == 1) {
                        objRoot.put("responseCode", "SUCCESS");
                        objRoot.put("wideManagerId", data.getStringExtra("wideManagerId"));
                        objRoot.put("couponNo", data.getStringExtra("couponNo"));
                        objRoot.put("membershipCustomerNo", String.valueOf(data.getIntExtra("membershipCustomerNo", 0)));
                        objRoot.put("couponDiscountTypeCode", data.getStringExtra("couponDiscountTypeCode"));

                        Log.d(TAG, "혜택 insert: " + objRoot.toString());
                        mWebView.postUrl(DOMAIN_NAME + GAME_BENEFIT_REGISTER_PATH, EncodingUtils.getBytes(objRoot.toString(), "UTF-8"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case 77:
                Log.d(TAG, "게임2 종료");

                try {
                    if (resultCode == 1) {
                        objRoot.put("responseCode", "SUCCESS");
                        objRoot.put("wideManagerId", data.getStringExtra("wideManagerId"));
                        objRoot.put("couponNo", String.valueOf(data.getIntExtra("couponNo", 0)));
                        objRoot.put("membershipCustomerNo", String.valueOf(data.getIntExtra("membershipCustomerNo", 0)));
                        objRoot.put("couponDiscountTypeCode", data.getStringExtra("couponDiscountTypeCode"));

                        Log.d(TAG, "혜택 insert: " + objRoot.toString());
                        mWebView.postUrl(DOMAIN_NAME + GAME_BENEFIT_REGISTER_PATH, EncodingUtils.getBytes(objRoot.toString(), "UTF-8"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //게임 설정 가져오기
    public class GameSettingTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "GameSettingTask";
        private final String mWideMangerId;


        public GameSettingTask(String managerId) {
            this.mWideMangerId = managerId;
        }


        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();

            Log.d(TAG, "" +
                    "" +
                    "" +
                    " 확인: " + mWideMangerId);
            Log.d(TAG, "wideCustomerNo 확인: " + wideCustomerVO.getWideCustomerNo());
            Log.d(TAG, "wideCustomerPhone 확인: " + wideCustomerVO.getWideCustomerPhone());

            sbParams.append("wideManagerId").append("=").append(mWideMangerId);
            sbParams.append("&");
            sbParams.append("wideCustomerNo").append("=").append(wideCustomerVO.getWideCustomerNo());
            sbParams.append("&");
            sbParams.append("wideCustomerPhone").append("=").append(wideCustomerVO.getWideCustomerPhone());

            try {
                URL url = new URL(DOMAIN_NAME + GAME_SETTING_PATH);
                urlConn = (HttpURLConnection) url.openConnection();

                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Accept-Charset", "UTF-8");

                String strParams = sbParams.toString();
                OutputStream os = urlConn.getOutputStream();
                os.write(strParams.getBytes("UTF-8"));
                os.flush();
                os.close();
                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return null;

                // [2-4]. 읽어온 결과물 리턴.
                // 요청한 URL의 출력물을 BufferedReader로 받는다.
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                // 출력물의 라인과 그 합에 대한 변수.
                String line;
                String page = "";

                // 라인을 받아와 합친다.
                while ((line = reader.readLine()) != null) {
                    page += line;
                }

                return page;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Log.e(TAG, s);
                try {
                    JSONObject objRoot = new JSONObject(s);

                    String responseCode = objRoot.optString("responseCode");

                    Log.d(TAG, "응답코드 확인: " + responseCode);
                    if (responseCode.equals("0")) {
                        //게임실행오류 alert;
                        return;
                    }

                    String gameSetting = objRoot.getString("gameSetting");
                    String membershipCustomerVO = objRoot.getString("membershipCustomerVO");
                    Log.d(TAG, "게임설정 확인: " + gameSetting);
                    Log.d(TAG, "멤버십고객 확인: " + membershipCustomerVO);

                    Intent intent = null;

                    switch (objRoot.getJSONObject("gameSetting").optString("markerGameTypeCode")) {
                        case "1":
                            //게임 실행!
                            intent = new Intent(MainActivity.this, Game1.class);
                            intent.putExtra("gameSetting", gameSetting);
                            intent.putExtra("membershipCustomerVO", membershipCustomerVO);

                            startActivityForResult(intent, 7);
                            break;
                        case "2":
                            //게임 실행!
                            intent = new Intent(MainActivity.this, Game2.class);
                            intent.putExtra("gameSetting", gameSetting);
                            intent.putExtra("membershipCustomerVO", membershipCustomerVO);

                            startActivityForResult(intent, 77);
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void progressON(Activity activity, String message) {
        if (activity == null || activity.isFinishing()) {
            Log.d(TAG, "액티비티 return");
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            Log.d(TAG, "progressDialog is not null");
            progressSET(message);
        } else {
            Log.d(TAG, "progressDialog is null");
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }


    private void progressSET(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }


        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }


    private void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}