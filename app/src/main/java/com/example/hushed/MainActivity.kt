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
import kotlin.collections.HashMap

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

    private var dummyData = dummyMessages.map {it.sender to (arrayListOf(hashMapOf("received" to it.message)))}.toMap()

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
                    // Get current list of conversations
                    // initially, this is empty.
                    var conversationList = DataSource.getConversationList()
                    var conversationMap = DataSource.getConversations()
                    var ownId = DataSource.getDeviceID()

                    for((key, value) in doc.data.orEmpty()) {
                        var allMessages = doc.get(key) as ArrayList<HashMap<*, *>>
                        var convo = ArrayList<Messages>()
                        var partnerId = key


                        Log.i("messages", "Got " + allMessages.size + " messages for convo with " + key)
                        // Load all messages in conversation into memory
                        for (message in allMessages) {
                            var str = "[NO MESSAGE]"
                            var sender = "[NO SENDER]"

                            if (message.containsKey("received") && message["received"] is String) {
                                str = message["received"] as String
                                sender = partnerId
                            }
                            if (message.containsKey("sent") && message["sent"] is String) {
                                str = message["sent"] as String
                                sender = ownId
                            }

                            var msg = Messages(
                                sender = sender,
                                message = str
                            )
                            convo.add(msg)
                        }

                        // Add modified version of last message from conversation to list
                        // where we set the 'sender' to be the partner,
                        // regardless of who actually sent that message
                        // so when it is displayed in the list,
                        // it shows the user who the conversation is with
                        // todo: translate ID to name of partner, and display that instead
                        var lastMsg = convo[convo.size-1]
                        conversationList.add(Messages(lastMsg.message, partnerId))

                        // Store entire conversation into dataset
                        conversationMap[partnerId] = convo

                    }

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

            for((key, value) in dummyData) {
                db.document(DataSource.getDeviceID()).set(hashMapOf(key to value),
                    SetOptions.merge())
                    .addOnSuccessListener { Log.d("Firebase", "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w("Firebase", "Error writing document", e) }
            }

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
