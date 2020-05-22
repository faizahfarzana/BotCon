package com.example.trial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashScreen.this, FindPairedDevices.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
