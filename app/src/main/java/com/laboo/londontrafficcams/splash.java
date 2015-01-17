package com.laboo.londontrafficcams;

/**
 * Created by VaiosK on 07/01/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splash extends Activity {
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(splash.this, MainActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);



    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}