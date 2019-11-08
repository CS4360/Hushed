package com.example.hushed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends Activity {
    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {

            Log.i("tag","Splash Activity Entered");
            setContentView(R.layout.activity_splash);

            mWaitHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    //The following code will execute after the 3 seconds.

                    try {
                        Log.i("tag","Nickname Activity Entered");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }, 3000);

            settings.edit().putBoolean("my_first_time", false).commit();
        }

        else {
            Log.i("tag","Splash Activity Skipped - MessageActivity Entered");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Remove all the callbacks otherwise navigation will execute even after activity is killed or closed.
        mWaitHandler.removeCallbacksAndMessages(null);
    }
}

// https://stackoverflow.com/questions/4636141/determine-if-android-app-is-the-first-time-used
