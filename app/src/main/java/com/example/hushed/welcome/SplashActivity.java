package com.example.hushed.welcome;

import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.hushed.R;
import com.example.hushed.contacts.NicknameActivity;
import com.example.hushed.crypto.Keygen;
import com.example.hushed.database.DataSource;
import com.example.hushed.messages.ConversationsActivity;


public class SplashActivity extends Activity {
    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefFile = getSharedPreferences("SplashActivityPrefsFile", 0);

        super.onCreate(savedInstanceState);

        if (prefFile.getBoolean("First_Time", true)) {
            Log.i("Activity","Entering Splash Activity");

            DataSource.Companion.setDeviceID(prefFile);
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

                        Log.i("Activity","Entering Nickname Activity");
                        Intent intent = new Intent(getApplicationContext(), NicknameActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }, 3000);
        }
        else {
            Log.i("Activity","Entering Conversations Activity");
            Intent intent = new Intent(getApplicationContext(), ConversationsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWaitHandler.removeCallbacksAndMessages(null);
    }
}