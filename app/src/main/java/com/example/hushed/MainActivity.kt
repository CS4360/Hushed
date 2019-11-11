package com.example.hushed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity

import java.util.*
import java.text.SimpleDateFormat

import kotlin.collections.HashMap
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate

import kotlinx.android.synthetic.main.activity_main.*

import com.example.hushed.models.Messages

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
        .collection("db")
    private var timer: Timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        DataSource.setDeviceID(checkAddress().toString())

        button_connect.setOnClickListener {
            Log.i("Button", "Click: Connect button clicked")

            db.document(DataSource.getDeviceID()).get()
                .addOnSuccessListener { doc ->
                    onLoadedDocument(doc)

                    refreshConversations()

                    val intent = Intent(this, MessageActivity::class.java)
                    startActivity(intent)

                }
                .addOnFailureListener { e ->
                    Log.w(
                        "Firebase",
                        "Error retrieving document from database", e
                    )
                }
        }

        button_settings.setOnClickListener {
            Log.i("Button", "Click: Settings button clicked")
            var intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        button_about.setOnClickListener {
            Log.i("Button", "Click: About button clicked")

            var date = Date()
            val formatter = SimpleDateFormat("MM/dd/yy HH:mm:ss:SSS a")
            val timestamp: String = formatter.format(date)

            Log.i("Time", "Current time is $timestamp")
        }
    }

    private fun checkAddress(): String? {
        val prefInfo = getPreferences(Context.MODE_PRIVATE)

        if (prefInfo.getString("UUID", null) != null) {
            return prefInfo.getString("UUID", null)
        } else {
            var myID = UUID.randomUUID().toString()
            prefInfo?.edit()?.putString("UUID", myID)?.apply()
            return myID
        }
    }

    private fun refreshConversations() {
        // Refreshes conversations every 10 seconds with data from the database
        // We can decrease the interval to see faster responses.
        // Ex. var task = timer.scheduleAtFixedRate(1*1000L, 1 * 1000L)
        timer.scheduleAtFixedRate(10 * 1000L, 10 * 1000L) {


            Log.i("Conversations", "Refreshing conversations")
            db.document(DataSource.getDeviceID()).get()
                .addOnSuccessListener(::onLoadedDocument)
        }
    }

    private fun onLoadedDocument(doc: DocumentSnapshot) {
        var conversationChanged = false
        var conversationList = DataSource.getConversationList()
        var conversationMap = DataSource.getConversations()

        Log.i("Database", "Got new data!")

        var id = DataSource.getDeviceID()

        var updates = HashMap<String, Any>()

        for ((key, value) in doc.data.orEmpty()) {
            if (!conversationMap.containsKey(key)) {
                conversationMap[key] = ArrayList()
            }

            updates[key] = FieldValue.delete()

            var newMessages =
                (doc.get(key) as HashMap<Any, Any>).toSortedMap(compareBy { it as Comparable<*> })
            var partnerId = key
            var conversation = conversationMap[key]!!

            conversationChanged = key.equals(DataSource.getViewingConversation())

            Log.i("Database", "New messages for ${partnerId}!")
            for ((timestamp, message) in newMessages) {
                conversation.add(
                    Messages(
                        timestamp = timestamp as String,
                        sender = partnerId,
                        message = message as String
                    )
                )
            }

            conversation.sortWith(Messages.comparator)

            var lastMsg = conversation[conversation.size - 1]
            var msg = Messages(
                message = lastMsg.message,
                sender = partnerId,
                timestamp = lastMsg.timestamp
            )

            var i = conversationList.indexOfFirst { message -> message.sender == partnerId }
            if (i >= 0) {
                conversationList.removeAt(i)
            }
            conversationList.add(0, msg)
        }

        db.document(id).update(updates)
        conversationList.sortWith(Messages.comparator)

        if (conversationChanged) {
            DataSource.conversationUpdated()
        }
    }
}