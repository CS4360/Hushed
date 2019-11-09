package com.example.hushed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;

import com.example.hushed.crypto.Keygen;

public class SplashActivity extends Activity {
    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            setContentView(R.layout.activity_splash);

            mWaitHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] privateKey = Keygen.generatePrivateKey();
                        byte[] publicKey = Keygen.generatePublicKey(privateKey);

                        String privKey = Keygen.byteToString(privateKey);
                        String pubKey = Keygen.byteToString(publicKey);

                        DataSource.Companion.saveKeys(getSharedPreferences("DeviceKeys", Context.MODE_PRIVATE), privKey, pubKey);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }, 3000);

            settings.edit().putBoolean("my_first_time", false).apply();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWaitHandler.removeCallbacksAndMessages(null);
    }
}
