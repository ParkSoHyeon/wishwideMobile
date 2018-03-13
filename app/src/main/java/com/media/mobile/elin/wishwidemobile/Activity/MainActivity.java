package com.media.mobile.elin.wishwidemobile.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.*;
import android.widget.ProgressBar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.media.mobile.elin.wishwidemobile.Model.Beacon_Marker;
import com.media.mobile.elin.wishwidemobile.Model.WideCustomerVO;
import com.media.mobile.elin.wishwidemobile.R;
import com.media.mobile.elin.wishwidemobile.SharedPreferencesConstant;
import com.media.mobile.elin.wishwidemobile.WebUrlConstance;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WebUrlConstance, SharedPreferencesConstant{
    private static final String TAG = "MainActivity";

    private final Context mContext = this;

    private SharedPreferences mSharedPreferences;
    private WideCustomerVO wideCustomerVO;

    //TopBar 관련 멤버변수
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private TabLayout mTabLayout;

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

    private final PermissionListener mLocationPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Log.d(TAG, "권한 허용");

            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 100, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, mLocationListener);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Log.d(TAG, "권한 거부");
            mWebView.loadUrl(DOMAIN_NAME + HOME_PATH + "?lat=0&lng=0");
        }
    };

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View 초기화
        initializeView();


        //메뉴탭 Listener
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected()..." + tab.getText());

                switch (tab.getText().toString()) {
                    case "홈":
                        requestLocationUpdate();
                        break;
                    case "방문한 매장":
                        mWebView.loadUrl(DOMAIN_NAME + VISITED_STORE_LIST_PATH);
                        break;
                    case "선물가게":
                        mWebView.loadUrl(DOMAIN_NAME + GIFT_STORE_LIST_PATH);
                        break;
                    case "도장/포인트":
                        mWebView.loadUrl(DOMAIN_NAME + STAMP_AND_POINT_LIST_PATH);
                        break;
                    case "쿠폰":
                        mWebView.loadUrl(DOMAIN_NAME + COUPON_LIST_PATH);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabUnselected()..." + tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabReselected()...");
            }
        });


        //AR 게임 실행 버튼 Listener
        mARFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GameSettingTask().execute();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });


        //위치 Listener
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(TAG, "위치 확인:" + location.getLatitude() + ", " + location.getLongitude());

                mWebView.loadUrl(DOMAIN_NAME + HOME_PATH + "?lat=" + location.getLatitude() + "&lng=" + location.getLongitude());
                requestRemoveUpdate();

//                new WebGET().execute(location.getLatitude(),location.getLongitude());
//                startBeaconScan();
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
                String str = peripheral.getBDAddress().replace(":", "");
                Log.d(TAG, "비콘 스캔" + str);
                if (!scanBeaconAll.contains(str))
                    scanBeaconAll.add(str);
                if (scanBeaconList.contains(str)) {
                    scanBeaconDistance.set(scanBeaconList.indexOf(str), peripheral.getDistance());
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
                mTabLayout.setVisibility(View.VISIBLE);
                mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);

                switch (url) {
                    case DOMAIN_NAME:   //로그인
                        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);    //menu(navigation) gone setting
                        mTabLayout.setVisibility(View.GONE);    //tab menu gone setting
                        break;

                    case DOMAIN_NAME + VISITED_STORE_LIST_PATH: //방문한 매장
                        break;

                    case DOMAIN_NAME + STORE_DETAIL_PATH:   //매장상세
                        mARFloatingActionButton.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }

                //AR 게임 실행 버튼 visible
                if (url.contains(DOMAIN_NAME + STORE_DETAIL_PATH)) {
                    mARFloatingActionButton.setVisibility(View.VISIBLE);
                }
                else if (url.contains(DOMAIN_NAME + HOME_PATH)) {
                    mWebView.clearHistory();
                    requestLocationUpdate();
                }
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

        }
        else if (responseCode.equals("LOGIN")) {
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
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);    //menu(navigation) visible/gone setting

        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_web_loading);
        mProgressBar.setMax(100);

        mARFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        mWebView = (WebView) findViewById(R.id.web_view);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebAndAppBridge = new WebAndAppBridge(mWebView);
        mWebView.addJavascriptInterface(mWebAndAppBridge, "WebAndAppBridge");
    }


    //Web의 javascript와 앱을 연결해주는 클래스
    private class WebAndAppBridge {
        private static final String REQUEST_EVENT = "request";
        private static final String PERMISSION_DENIED_EVENT = "denied";
        private static final String PERMISSION_GRANTED_EVENT = "granted";

        private final WebView mWebView;
        private int mGiftProductNo;

        private double mLatitude;
        private double mLongitude;

        public WebAndAppBridge(WebView webView) {
            mWebView = webView;
        }

        final PermissionListener contactPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "권한 허용");

                getAndroidContactList(PERMISSION_GRANTED_EVENT, mGiftProductNo);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Log.d(TAG, "권한 거부");

                getAndroidContactList(PERMISSION_DENIED_EVENT, mGiftProductNo);
            }
        };

        @JavascriptInterface
        public void getAndroidContactList(String event, int giftProductNo) {
            Log.d(TAG, "연락처 가져오기");

            JSONObject objRoot = new JSONObject();

            try {
                switch (event) {
                    case REQUEST_EVENT:
                        requestPermission(contactPermissionListener, Manifest.permission.READ_CONTACTS);
                        mGiftProductNo = giftProductNo;
                        objRoot.put("responseCode", "HOLD");
                        break;
                    case PERMISSION_GRANTED_EVENT:
                        objRoot.put("contacts", getContactAll());
                        objRoot.put("giftProductNo", mGiftProductNo);
                        objRoot.put("responseCode", "SUCCESS");

                        Log.d(TAG, "byte: " + EncodingUtils.getBytes(objRoot.toString(), "UTF-8"));
                        Log.d(TAG, "string: " + objRoot.toString());
                        mWebView.postUrl(DOMAIN_NAME + CONTACT_LIST_PATH, EncodingUtils.getBytes(objRoot.toString(), "UTF-8"));
                        break;
                    case PERMISSION_DENIED_EVENT:
                        objRoot.put("responseCode", "DENIED");
                        objRoot.put("giftProductNo", mGiftProductNo);

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

                    Log.d(TAG, idContact);
                    Log.d(TAG, name);
                    Log.d(TAG, phoneNumber);

                    objContact.put("contactId", idContact);
                    objContact.put("contactName", name);
                    objContact.put("contactPhone", phoneNumber);


                    arrContacts.put(objContact);
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
                editor.commit();

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

            switch (mWebView.getUrl()) {
                case DOMAIN_NAME + GIFT_DETAIL_PATH:
                    mWebView.goBackOrForward(-1);
                    // history 삭제
                    mWebView.clearHistory();
                    break;
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        TabLayout.Tab tab;

        switch (id) {
            case R.id.nav_visited_store:    //방문한매장
//                mWebView.loadUrl(DOMAIN_NAME + VISITED_STORE_LIST_PATH);
                tab = mTabLayout.getTabAt(1);
                tab.select();
                break;
            case R.id.nav_nearby_stores:    //홈
                requestLocationUpdate();
                tab = mTabLayout.getTabAt(0);
                tab.select();
                break;
            case R.id.nav_gift_store:   //선물가게
//                mWebView.loadUrl(DOMAIN_NAME + GIFT_STORE_LIST_PATH);
                tab = mTabLayout.getTabAt(2);
                tab.select();
                break;
            case R.id.nav_received_gift:    //받은선물내역(선물함)
                mWebView.loadUrl(DOMAIN_NAME + RECEIVED_GIFT_LIST_PATH);
                break;
            case R.id.nav_send_gift:    //보낸선물내역
                mWebView.loadUrl(DOMAIN_NAME + SEND_GIFT_LIST_PATH);
                break;
            case R.id.nav_coupon:   //쿠폰함내역(쿠폰함)
//                mWebView.loadUrl(DOMAIN_NAME + COUPON_LIST_PATH);
                tab = mTabLayout.getTabAt(3);
                tab.select();
                break;
            case R.id.nav_point_and_stamp:  //도장/포인트내역
//                mWebView.loadUrl(DOMAIN_NAME + STAMP_AND_POINT_LIST_PATH);
                tab = mTabLayout.getTabAt(4);
                tab.select();
                break;
            case R.id.nav_setting:  //환경설정
                //setting activity 이동
                break;
            default:
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
        int locationPermissionCheck1 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int locationPermissionCheck2 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (locationPermissionCheck1 == PackageManager.PERMISSION_GRANTED && locationPermissionCheck2 == PackageManager.PERMISSION_GRANTED) {
            //위치 권한 있음
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

            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 100, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, mLocationListener);
        } else {
            requestPermission(mLocationPermissionListener, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_CHECKIN_PROPERTIES);
        }
    }


    //위치 서비스 종료
    private void requestRemoveUpdate() {
        mLocationManager.removeUpdates(mLocationListener);
    }


    //권한 요청
    private void requestPermission(PermissionListener permissionListener, String... permissions) {
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
//                .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                .setDeniedMessage("거부하신 권한은 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(permissions)
                .check();
    }


    public class BeaconFileRead extends AsyncTask<Void, Void, String> {

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

                JSONArray ja = new JSONArray(str);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    Log.d(TAG, "Beacon.txt 파일 읽음: " + jo.getString("ww_beacon_macAddress"));
                    scanBeaconList.add(jo.getString("ww_beacon_macAddress"));
                    scanBeaconDistance.add(999.0);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public class NearbyBeaonListTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... params) {
            double minla = params[0] - 1, maxla = params[0] + 1, minlo = params[1] - 1, maxlo = params[1] + 1;
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();

            sbParams.append("minla").append("=").append(minla).append("&");
            sbParams.append("maxla").append("=").append(maxla).append("&");
            sbParams.append("minlo").append("=").append(minlo).append("&");
            sbParams.append("maxlo").append("=").append(maxlo);

            try {
                URL url = new URL("http://192.168.0.23:8080/databeacon");
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
        beaconScanResult = new BeaconScanResult();
        beaconScanResult.start();
        centralManager.startScanning();
    }


    //비콘 스캔 종료 및 자원 반납
    private void stopBeaconScan() {
        beaconScanResult.isCon = false;
        beaconScanResult = null;
        centralManager.stopScanning();
    }


    public class BeaconScanResult extends Thread {
        public boolean isCon = true;

        @Override
        public void run() {
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
        switch (requestCode) {
            case 7:
                JSONObject objRoot = new JSONObject();

                try {
                    if (resultCode == 1) {
                        objRoot.put("responseCode", "SUCCESS");
                        objRoot.put("wideManagerId", data.getStringExtra("wideManagerId"));
                        objRoot.put("wwNo", data.getIntExtra("wwNo", 0));
                        objRoot.put("markerNo", data.getIntExtra("markerNo", 0));
                        objRoot.put("gameBenefitTypeCode", data.getStringExtra("gameBenefitTypeCode"));
                        objRoot.put("membershipCustomerNo", data.getIntExtra("membershipCustomerNo", 0));
                        objRoot.put("gameBenefitTypeValue", data.getIntExtra("gameBenefitTypeValue", 0));

                        Log.d(TAG, "혜택 insert: " + objRoot.toString());
                        mWebView.postUrl(DOMAIN_NAME + GAME_BENEFIT_REGISTER_PATH, EncodingUtils.getBytes(objRoot.toString(), "UTF-8"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    public class GameSettingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();

            sbParams.append("wideManagerId").append("=").append("starbucksJuk");
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

                    //게임 실행!
                    Intent intent = new Intent(MainActivity.this, VideoPlayback.class);
                    intent.putExtra("gameSetting", gameSetting);
                    intent.putExtra("membershipCustomerVO", membershipCustomerVO);

                    startActivityForResult(intent, 7);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
