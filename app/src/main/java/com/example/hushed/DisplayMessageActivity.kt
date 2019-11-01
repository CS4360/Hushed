package com.example.hushed

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.activity_message_chat.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DisplayMessageActivity : AppCompatActivity() {

    private lateinit var displayAdapter: DisplayRecyclerAdapter

    private var partnerId: String = ""
    private var partnerName: String = ""

    private val db = FirebaseFirestore.getInstance()
        .collection("db")

    // note from jon: logic to run when we receive an update from the database
    private var conversationUpdatedCallback = Runnable {
        displayAdapter.notifyDataSetChanged()
        // scroll adapter to bottom
        messageList.scrollToPosition(displayAdapter.itemCount - 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        initRecyclerView()

        // note from jon:
        // Get NAME extra, describing id of who the conversation is with
        partnerId = intent.getStringExtra(ID) ?: ""
        partnerName = intent.getStringExtra(NAME) ?: ""
        // Set the currently viewed conversation in the shared DataSource
        DataSource.setViewingConversation(partnerId)

        // note from jon:
        // Set the name of the partner into the title bar
        // todo: map the id of the partner to their actual name
        val actionBar = supportActionBar
        actionBar!!.title = if (partnerName.isBlank()) "[NO PARTNER]" else partnerName
        initDataSet()

        btnSend.setOnClickListener {

            var date = Date()
            val formatter = SimpleDateFormat("MM/dd/yy HH:mm:ss:SSS a")
            val timestamp: String = formatter.format(date)

            Log.i("tag", "Click: send_button Button")
            if (txtMessage.text.isNullOrBlank()) {
                Toast.makeText(
                    this@DisplayMessageActivity,
                    "Message cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
                Log.i("tag", "Blank message entered")
            } else if (partnerId.isNullOrBlank()) {
                Toast.makeText(this, "Sender cannot be blank", Toast.LENGTH_LONG).show()
                Log.i("tag", "Blank Sender")
            } else {
                // Put all logic for both updating local data and database into one method
                sendMessage(txtMessage.text.toString(), timestamp)
            }

            txtMessage.text.clear()
        }
    }

    // note from jon: this lifecycle method gets called just before an activity begins running
    override fun onResume() {
        super.onResume()
        DataSource.addOnConversationUpdated(conversationUpdatedCallback)
    }

    // note from jon: this lifecycle method gets called just after an activity begins running
    override fun onPause() {
        super.onPause()
        DataSource.removeOnConversationUpdated(conversationUpdatedCallback)
    }

    private fun sendMessage(msg: String, timestamp: String) {
        // add a message to the local system
        val senderName: String = DataSource.getDeviceID()
        // add message to display
        displayAdapter.addMessage(msg, senderName, timestamp)

        var convoList = DataSource.getConversationList()
        var index = convoList.indexOfFirst { message -> message.sender == partnerId }
        if (index >= 0) {
            convoList.removeAt(index)
        }
        var msg = Messages(
            sender = partnerId,
            message = msg,
            timestamp = timestamp
        )
        convoList.add(msg)
        convoList.sortWith(Messages.comparator)

        DataSource.saveTo(getSharedPreferences("DataSource", Context.MODE_PRIVATE))
        // scroll adapter to bottom
        messageList.scrollToPosition(displayAdapter.itemCount - 1)

        // send message to database.
        //document(parnerID -> username -> UUID of person we want to connect to)
        //right now partnerId is the just the string of the person we want to connect to
        db.document(partnerId)
            .set(
                hashMapOf(DataSource.getDeviceID() to hashMapOf(timestamp to txtMessage.text.toString())),
                SetOptions.merge()
            )
            .addOnSuccessListener { Log.d("Firebase", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error writing document", e)
            }


    }

    // note from jon: Added this method to initialize the data set in the recycler view
    // We will grab the conversation that was set by
    private fun initDataSet() {
        var conversations = DataSource.getConversations()
        var convo = conversations[partnerId] ?: ArrayList()
        if (!conversations.containsKey(partnerId)) {
            conversations[partnerId] = convo
        }
        Log.i("messages", "Conversation with " + partnerId + " has " + convo.size + " Mesasges.")

        // send the list to the display adapter
        displayAdapter.submitList(convo)
        // Scroll to bottom
        messageList.scrollToPosition(displayAdapter.itemCount - 1)
    }

    private fun initRecyclerView() {
        messageList.apply {
            layoutManager = LinearLayoutManager(this@DisplayMessageActivity).apply {
                stackFromEnd = true
            }
            // Set 'this' DisplayMessageActivity's displayAdapter
            displayAdapter = DisplayRecyclerAdapter()
            // Set the 'messageList''s adapter
            adapter = displayAdapter
        }
    }
}
