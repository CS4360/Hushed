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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
        .collection("db")
    private val dummyMessages = listOf( Messages(
        sender = "Friend Unit 1",
        message = "Hey there!",
        timestamp = "01/01/01 00:00:00:00 a"
    ), Messages(
        sender = "Parental Unit 1",
        message = "Please call me back",
        timestamp = "01/01/01 00:00:00:00 a"
    ))

    private var timer: Timer = Timer()
    private var dummyData = dummyMessages.map {it.sender to (hashMapOf("01/01/01 00:00:00:00 AM"
            to it.message))}.toMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataSource.setDeviceID(checkAddress().toString())

        button_connect.setOnClickListener {
            Log.i("Button","Click: Connect button clicked")

            var conversationList = DataSource.getConversationList()
            var conversationMap = DataSource.getConversations()

            db.document(DataSource.getDeviceID()).get()
                .addOnSuccessListener { doc ->
                    for((key, value) in doc.data.orEmpty()) {
                        var partnerId = key
                        var conversation = ArrayList<Messages>()
                        var allMessages = (doc.get(key) as HashMap<Any, Any>).toSortedMap(
                            compareBy { it as Comparable<*> })

                        Log.i("Messages", "Got " + allMessages.size +
                                " messages for conversation with " + key)

                        for((timestamp, message) in allMessages) {
                            var message = extractMessage(hashMapOf(timestamp to message), partnerId)
                            if((message !in conversationList)) {
                                conversation.add(message)
                            }
                        }

                        if(conversation.isNotEmpty()) {
                            var lastMsg = conversation[conversation.size-1]
                            conversationList.add(Messages(lastMsg.message, partnerId,
                                lastMsg.timestamp))

                            conversationMap[partnerId] = conversation
                        }
                    }

                    refreshConversations()

                    val intent = Intent(this, MessageActivity::class.java)
                    startActivity(intent)

                }
                .addOnFailureListener { e -> Log.w("Firebase",
                    "Error retrieving document from database", e) }
        }

        button_settings.setOnClickListener {
            Log.i("Button","Click: Settings button clicked")
        }

        button_about.setOnClickListener {
            Log.i("Button","Click: About button clicked")

            var date = Date()
            val formatter = SimpleDateFormat("MM/dd/yy HH:mm:ss:SSS a")
            val timestamp: String = formatter.format(date)

            Log.i("Time", "Current time is $timestamp")

        }

        button_dummy.setOnClickListener {

            for((key, value) in dummyData) {
                db.document(DataSource.getDeviceID()).set(hashMapOf(key to value),
                    SetOptions.merge())
                    .addOnSuccessListener { Log.d("Firebase",
                        "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w("Firebase",
                        "Error writing document", e) }
            }

            Log.i("Button","Click: Dummy Send button clicked")
        }
    }

    private fun extractMessage(messageData: HashMap<*,*>, partnerId: String): Messages {
        var timestamp = messageData.keys.elementAt(0).toString()
        var str = messageData.get(timestamp).toString()
        var sender = partnerId

        return Messages(
            sender = sender,
            message = str,
            timestamp = timestamp
        )
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

    private fun refreshConversations() {
        // Refreshes conversations every 10 seconds with data from the database
        // We can decrease the interval to see faster responses.
        // Ex. var task = timer.scheduleAtFixedRate(1*1000L, 1 * 1000L)
        timer.scheduleAtFixedRate(10*1000L, 10 * 1000L) {
            var conversationChanged = false
            var conversationList = DataSource.getConversationList()
            var conversationMap = DataSource.getConversations()

            Log.i("Conversations", "Refreshing conversations")
            db.document(DataSource.getDeviceID()).get()
                .addOnSuccessListener { doc ->
                    Log.i("Database", "Got new data!")

                    for((key, value) in doc.data.orEmpty()) {
                        var allMessages = (doc.get(key) as HashMap<Any, Any>).toSortedMap(
                            compareBy { it as Comparable<*> })
                        var partnerId = key

                        if (conversationMap.containsKey(key)) {
                            var conversation = conversationMap[key]!!

                            if (allMessages.size > conversation.size) {
                                conversationChanged = key.equals(DataSource.getViewingConversation())

                                Log.i("Database", "New messages for ${partnerId}!")

                                for((timestamp, message) in allMessages) {
                                    conversation.add(extractMessage(hashMapOf(timestamp to message),
                                        partnerId))
                                }

                                var lastMsg = conversation[conversation.size-1]
                                var msg = Messages(message = "[NO MESSAGE]",
                                    sender = "[NO SENDER]",
                                    timestamp = "[NO TIMESTAMP]")

                                for (i in conversationList.indices) {
                                    if (conversationList[i].sender == partnerId) {
                                        msg = conversationList[i]
                                    }
                                }

                                conversationList.remove(msg)
                                conversationList.add(0, msg)
                                msg.message = lastMsg.message
                            }
                        }
                        else {
                            Log.i("Database", "New conversation with ${partnerId}!");
                            var conversation = ArrayList<Messages>()

                            for((timestamp, message) in allMessages) {
                                conversation.add(extractMessage(hashMapOf(timestamp to message),
                                    partnerId))
                            }

                            var lastMsg = conversation[conversation.size-1]
                            conversationList.add(0, Messages(lastMsg.message, partnerId,
                                lastMsg.timestamp))

                            conversationMap[partnerId] = conversation
                        }
                    }

                    if (conversationChanged) {
                        DataSource.conversationUpdated()
                    }
                }
        }
    }
}
