package com.media.mobile.elin.wishwidemobile.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.media.mobile.elin.wishwidemobile.PermissionConstant;
import com.media.mobile.elin.wishwidemobile.R;
import com.media.mobile.elin.wishwidemobile.SharedPreferencesConstant;

public class SettingActivity
        extends AppCompatActivity implements SharedPreferencesConstant {
    private TextView tvEmail, tvName, tvBirth, tvGender, tvAppVersion;
    private Button btnLogout;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvBirth = (TextView) findViewById(R.id.tv_birth);
        tvGender = (TextView) findViewById(R.id.tv_gender);
        tvAppVersion = (TextView) findViewById(R.id.tv_app_version);
        btnLogout = (Button) findViewById(R.id.btn_logout);

        mSharedPreferences = this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        String birth = mSharedPreferences.getString(WIDE_CUSTOMER_BIRTH_KEY, "");


        tvEmail.setText(mSharedPreferences.getString(WIDE_CUSTOMER_EMAIL_KEY, ""));
        tvName.setText(mSharedPreferences.getString(WIDE_CUSTOMER_NAME_KEY, ""));
        tvBirth.setText(
                birth.substring(0, 4)+"년 "
                +birth.substring(4, 6)+"월 "
                +birth.substring(6, 8)+"일");
        tvGender.setText(mSharedPreferences.getString(WIDE_CUSTOMER_SEX_KEY, "").equals("0") ? "남자" : "여자");
        tvAppVersion.setText("0.0");

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.clear();
                editor.commit();

                setResult(1);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(2);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
