package com.media.mobile.elin.wishwidemobile.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.media.mobile.elin.wishwidemobile.R;
import com.media.mobile.elin.wishwidemobile.SharedPreferencesConstant;

public class LoadingActivity extends AppCompatActivity implements SharedPreferencesConstant {
    private static final String TAG = "LoadingActivity";

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mSharedPreferences = this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        //preferences shared 사용해 로그인 이력 있는지 확인
        String wideCustomerPhone = mSharedPreferences.getString(WIDE_CUSTOMER_PHONE_KEY, "");

//        int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
//        if(permissionCamera == PackageManager.PERMISSION_DENIED) {
//            System.out.println("권한 없음 요청해야해");
//            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//            startActivity(intent);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 4);
//        } else {
//            System.out.println("권한 있음");
//            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//            startActivity(intent);
//        }

        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        Log.d(TAG, "전화번호 확인: " + wideCustomerPhone);
        if (!(wideCustomerPhone.equals(""))) {
            intent.putExtra("responseCode", "AUTO");
            intent.putExtra("wideCustomerPhone", wideCustomerPhone);
        }
        else {
            intent.putExtra("responseCode", "LOGIN");
        }

        startActivity(intent);
    }
}
