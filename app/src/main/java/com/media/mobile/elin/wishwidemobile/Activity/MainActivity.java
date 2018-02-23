package com.media.mobile.elin.wishwidemobile.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.media.mobile.elin.wishwidemobile.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    //SERVER URL 관련 멤버변수
    private String loadUrl = "http://220.230.113.159:8080/";    //실서버
//    private String loadUrl = "http://220.149.254.76/";  //회사 서버

    //TopBar 관련 멤버변수
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    //FloatingActionButton 관련 멤버 변수
    FloatingActionButton ARFloatingActionButton;

    //WebView 관련 멤버변수
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = getIntent();
//        if (intent != null) {
//            loadUrl = intent.getStringExtra("loadUrl");
//        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ARFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        ARFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                startActivity(new Intent(MainActivity.this, VideoPlayback.class));
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.pb_web_loading);
        progressBar.setMax(100);

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
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
            }

        });

        webView.loadUrl(loadUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (webView.canGoBack())) {
            Log.d(TAG, "들어옴");
            webView.goBack();

            return true;
        }
        else {
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
                    .setNegativeButton("아니오",  null).show();
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

        switch (id) {
            case R.id.nav_visited_store:    //방문한매장
                webView.loadUrl(loadUrl + "mobile/home/listVisitedStore");
                break;
            case R.id.nav_nearby_stores:    //주변매장
                webView.loadUrl(loadUrl + "mobile/home/listNearbyStore");
                break;
            case R.id.nav_gift_store:   //선물가게
                webView.loadUrl(loadUrl + "mobile/gift/listGiftStore");
                break;
            case R.id.nav_received_gift:    //받은선물내역(선물함)
                webView.loadUrl(loadUrl + "mobile/gift/listReceivedGift");
                break;
            case R.id.nav_send_gift:    //보낸선물내역
                webView.loadUrl(loadUrl + "mobile/gift/listSendGift");
                break;
            case R.id.nav_coupon:   //쿠폰함내역(쿠폰함)
                webView.loadUrl(loadUrl + "mobile/benefit/listCoupon");
                break;
            case R.id.nav_point_and_stamp:  //도장/포인트내역
                webView.loadUrl(loadUrl + "mobile/benefit/listStampAndPoint");
                break;
            case R.id.nav_setting:  //환경설정
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
