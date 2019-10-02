package com.example.hushed

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
        .collection("db")
    private val dummyMessages = listOf( Messages(
        sender = "Frient Unit 1",
        message = "Hey there!"
    ), Messages(
        sender = "Parental Unit 1",
        message = "Please call me back"
    ), Messages(
        sender = "Friend Unit 2",
        message = "Just wanted to let you know..."
    ), Messages(
        sender = "Sibling Unit 1",
        message = "Please don't tell Parental Unit 1 about this"
    ), Messages(
        sender = "Friend Unit 3",
        message = "Bruh!"
    ), Messages(
        sender = "Group Member 1",
        message = "Need the report to be finished soon"
    ), Messages(
        sender = "Parental Unit 2",
        message = "See you this weekend"
    ), Messages(
        sender = "Gandalf the Grey",
        message = "You Shall Not PASS!"
    ))

    private var dummyData = dummyMessages.map {it.sender to it.message}.toMap()

    private val messages = ArrayList<Messages>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DataSource.setDeviceID(checkAddress().toString())

        // CONNECT BUTTON **************************************************************************
        button_connect.setOnClickListener {
            Log.i("Button","Click: button_connect")

            db.document(DataSource.getDeviceID()).get()
                .addOnSuccessListener { doc ->
                    for((key, value) in doc.data.orEmpty()) {
                        messages.add(Messages(
                            sender = key,
                            message = value.toString()
                        ))
                    }
                    DataSource.setDataSet(messages)
                    val intent = Intent(this, MessageActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e -> Log.w("Firebase", "Error retrieving document", e) }
        }

        // SETTINGS BUTTON **************************************************************************
        button_settings.setOnClickListener {
            println("SETTINGS BUTTON")
            Log.i("Button","Click: button_settings")
        }

        // ABOUT BUTTON **************************************************************************
        button_about.setOnClickListener {
            Log.i("Button","Click: button_about")
        }

        // Dummy Send BUTTON **************************************************************************
        button_dummy.setOnClickListener {
            db.document(DataSource.getDeviceID()).set(dummyData, SetOptions.merge())
                .addOnSuccessListener { Log.d("Firebase", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("Firebase", "Error writing document", e) }

            Log.i("Button","Click: button_dummy")
        }
    }

    private fun checkAddress(): String? {
        val prefInfo = getPreferences(Context.MODE_PRIVATE)

        if(prefInfo.getString("UUID", null) != null) {
            return prefInfo.getString("UUID", null)
        }
        else {
            var myID = UUID.randomUUID().toString()
            prefInfo?.edit()?.putString("UUID", myID)?.apply()
            return myID
        }
    }
}
