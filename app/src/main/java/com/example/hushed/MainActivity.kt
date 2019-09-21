package com.example.hushed

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MainActivity : AppCompatActivity() {
    var dummyAddress = ""
    var dummyData = hashMapOf<String, Map<String, Any>>()

    val db = FirebaseFirestore.getInstance()
        .collection("db")
        .document("collection")
    val map = mutableMapOf<String, Any?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
            } else { ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), 2) } }

        val displayIMEI = retrieveImei()

        // CONNECT BUTTON **************************************************************************
        button_connect.setOnClickListener {
            Log.i("Button","Click: button_connect")
            val intent = Intent(this, MessageActivity::class.java)
            startActivity(intent)
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

        // Dummy BUTTON **************************************************************************
        button_dummy.setOnClickListener {
            if (displayIMEI != null) {
                this.dummyAddress = displayIMEI
            }

            this.dummyData = hashMapOf(dummyAddress to hashMapOf("Brian" to "This is from the emulator", "Francisco" to "I am just testing stuff... wait a second"))

            db.set(dummyData, SetOptions.merge())
                .addOnSuccessListener { Log.d("Firebase", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("Firebase", "Error writing document", e) }

            Log.i("Button","Click: button_dummy")
        }

        // Retrieve BUTTON **************************************************************************
        button_retrieve.setOnClickListener {
            db.get()
                .addOnSuccessListener { doc ->
                    map.put(dummyAddress, doc[dummyAddress])

                    if(map[dummyAddress] != null) {
                        textView4.setText(map[dummyAddress].toString())
                    }
                }
                .addOnFailureListener { e -> Log.w("Firebase", "Error retrieving document", e) }

            Log.i("Button","Click: button_retrieve")
        }
    }

    fun retrieveImei(): String? {
        try{
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            val test = tm.getImei()
            val id = tm.getDeviceId()
            return id
        }catch (ex:Exception){
            Log.i("", "There was a problem")
        }
        return "ERROR" // not sure what this should return
    }
}
