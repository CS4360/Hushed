package com.example.hushed.contacts;

import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.content.Context;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hushed.messages.ConversationsActivity;
import com.example.hushed.database.DataSource;
import com.example.hushed.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class NicknameActivity extends AppCompatActivity {
    private CollectionReference nicknames = FirebaseFirestore.getInstance()
            .collection("nicknames");

    private TextView nicknameMessage;
    private TextView nicknameDisplay;
    private Button nicknameButton;
    private ProgressBar nicknameProgress;
    private EditText nickname;
    private Handler mWaitHandler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nickname);

        SharedPreferences nickNameFile = getSharedPreferences("NickNamePrefFile", 0);

        nicknameButton = findViewById(R.id.nicknameButton);
        nicknameMessage = findViewById(R.id.nicknameMessage);
        nicknameProgress = findViewById(R.id.nicknameProgress);
        nicknameDisplay = findViewById(R.id.myOwnNickName);
        nickname = findViewById(R.id.nickname);

        String myNickname = DataSource.Companion.getDeviceNickName(nickNameFile);

        if(!myNickname.equals("NO_NICKNAME")) {
//            nicknameDisplay.setText("Your Nickname is: " + myNickname);
            String display = getString(R.string.display_nickname_message, myNickname);
            nicknameDisplay.setText(display);
        }
        else {
            String display = getString(R.string.display_default_nickname);
            nicknameDisplay.setText(display);
        }

        nicknameButton.setOnClickListener(this::onNicknameButtonClicked);
    }

    private void onNicknameButtonClicked(View clickedView) {
        String requestedNickname = nickname.getText().toString();

        if(requestedNickname.isEmpty() || requestedNickname == null) {
            nicknameMessage.setText("Please provide a nickname");
        }
        else {
            nicknameButton.setEnabled(false);
            nickname.setEnabled(false);
            nicknameProgress.setVisibility(View.VISIBLE);
            nicknameMessage.setText("Loading nicknames...");

            nicknames.document(requestedNickname)
                    .get()
                    .addOnSuccessListener((doc) -> {
                        onNicknamesLoaded(doc, requestedNickname);
                    })
                    .addOnFailureListener((err) -> {
                        Log.e("Test", "Failed to get " + requestedNickname + ": " + err);
                    });
        }
    }

    private void onNicknamesLoaded(DocumentSnapshot doc, String requestedNickname) {
        SharedPreferences prefFile = getSharedPreferences("SplashActivityPrefsFile", 0);
        SharedPreferences nickNameFile = getSharedPreferences("NickNamePrefFile", 0);
        String requestedName = nickname.getText().toString();
        String id = DataSource.Companion.getDeviceID(prefFile);

        String publicKey = DataSource.Companion.getPublicKey(getSharedPreferences("DeviceKeys", Context.MODE_PRIVATE));

        nicknames.whereEqualTo("id", id)
                .get()
                .addOnSuccessListener((query) -> {
                    boolean hasOldName = query.size() > 0;
                    String oldName = hasOldName ? query.getDocuments().get(0).getId() : null;

                    Map<String, Object> data = doc.getData();

                    if (data == null || data.size() == 0) {
                        Map<String, Object> setData = new HashMap<>();
                        setData.put("id", id);
                        setData.put("publicKey", publicKey);

                        if (oldName != null) {
                            nicknames.document(oldName).delete();
                        }

                        nicknames.document(requestedName)
                                .set(setData, SetOptions.merge())
                                .addOnSuccessListener((succ) -> {
                                    nicknameProgress.setVisibility(View.GONE);
                                    nickname.setEnabled(true);
                                    nicknameButton.setEnabled(true);
                                    nicknameMessage.setText("Congrats, you are now " + requestedName + "!");
                                    nickNameFile.edit().putString("myNickName", requestedNickname).apply();
                                    prefFile.edit().putBoolean("First_Time", false).apply();

                                    String myNickname = DataSource.Companion.getDeviceNickName(nickNameFile);
                                    String display = getString(R.string.display_nickname_message, myNickname);
                                    nicknameDisplay.setText(display);

                                    startConversationsActivity();
                                })
                                .addOnFailureListener((err) -> {
                                    nicknameProgress.setVisibility(View.GONE);
                                    nickname.setEnabled(true);
                                    nicknameButton.setEnabled(true);
                                    nicknameMessage.setText("An issue has occurred.  Please try again!");
                                });
                    }
                    else {
                        String otherId = (String) data.get("id");

                        if (otherId.equals(id)) {
                            nicknameMessage.setText("You already have that nickname!");
                            nickname.setEnabled(true);
                            nicknameButton.setEnabled(true);
                            nicknameProgress.setVisibility(View.GONE);
                        }
                        else {
                            nicknameMessage.setText("Somebody else has that name.  Please choose another name!");
                            nickname.setEnabled(true);
                            nicknameButton.setEnabled(true);
                            nicknameProgress.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void startConversationsActivity() {
        mWaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("Activity","Entering Conversations Activity");
                    Intent intent = new Intent(getApplicationContext(), ConversationsActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 2000);
    }
}
