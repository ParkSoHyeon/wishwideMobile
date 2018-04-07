package com.media.mobile.elin.wishwidemobile.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import com.media.mobile.elin.wishwidemobile.R;
import com.media.mobile.elin.wishwidemobile.SharedPreferencesConstant;

public class PermissionGuideActivity
        extends AppCompatActivity
        implements SharedPreferencesConstant {
    private ImageButton mIbtnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_guide);

        mIbtnOK = (ImageButton) findViewById(R.id.ibtn_ok);


        mIbtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
