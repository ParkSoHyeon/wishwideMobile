package com.media.mobile.elin.wishwidemobile.Activity;

import android.app.Application;
import com.tsengvn.typekit.Typekit;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumSquareR.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NanumSquareB.ttf"));
    }
}
