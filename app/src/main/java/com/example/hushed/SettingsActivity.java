package com.example.hushed;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class SettingsActivity extends AppCompatActivity {
    private CollectionReference db = FirebaseFirestore.getInstance()
            .collection("db");

    private TextView nicknameMessage;
    private Button nicknameButton;
    private ProgressBar nicknameProgress;
    private EditText nickname;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        nicknameButton = findViewById(R.id.nicknameButton);
        nicknameMessage = findViewById(R.id.nicknameMessage);
        nicknameProgress = findViewById(R.id.nicknameProgress);
        nickname = findViewById(R.id.nickname);

        nicknameButton.setOnClickListener(this::onNicknameButtonClicked);


    }

    private void onNicknameButtonClicked(View clickedView) {
        nicknameButton.setEnabled(false);
        nickname.setEnabled(false);
        nicknameProgress.setVisibility(View.VISIBLE);
        nicknameMessage.setText("Loading nicknames...");

        db.document("nicknames")
                .get()
                .addOnSuccessListener(this::onNicknamesLoaded);
    }

    private void onNicknamesLoaded(DocumentSnapshot doc) {
        String requestedName = nickname.getText().toString();
        String id = DataSource.Companion.getDeviceID();
        String oldName = null;

        Map<String, Object> data = doc.getData();
        for (String name : data.keySet()) {
            Log.i("Test", name + ": " + data.get(name));
            if (data.get(name).equals(id)) {
                oldName = name;
            }
        }
        if (oldName != null && oldName.equals(requestedName)) {
            nicknameMessage.setText("You already have that nickname!");
            nickname.setEnabled(true);
            nicknameButton.setEnabled(true);
            nicknameProgress.setVisibility(View.GONE);
            return;
        }
        if (data.containsKey(requestedName)) {
            nicknameMessage.setText("Someone else already has that name!");
            nickname.setEnabled(true);
            nicknameButton.setEnabled(true);
            nicknameProgress.setVisibility(View.GONE);
            return;
        }

        Map<String, Object> setData = new HashMap<>();
        if (oldName != null) {
            setData.put(oldName, FieldValue.delete());
        }
        setData.put(requestedName, id);

        db.document("nicknames")
                .set(setData, SetOptions.merge())
                .addOnSuccessListener((succ) -> {
                    nicknameProgress.setVisibility(View.GONE);
                    nickname.setEnabled(true);
                    nicknameButton.setEnabled(true);
                    nicknameMessage.setText("Congrats, you are now " + requestedName + "!");
                })
                .addOnFailureListener((err) -> {
                    nicknameProgress.setVisibility(View.GONE);
                    nickname.setEnabled(true);
                    nicknameButton.setEnabled(true);
                    nicknameMessage.setText("Sorry, you didn't get " + requestedName + "!");
                });

    }
}
