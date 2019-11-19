package com.example.hushed.messages

import java.util.Date
import java.text.SimpleDateFormat

import javax.crypto.Cipher

import com.example.hushed.crypto.EncDec
import com.example.hushed.crypto.Keygen
import com.example.hushed.models.Messages

import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FirebaseFirestore

import kotlin.collections.ArrayList

import kotlinx.android.synthetic.main.activity_message_chat.*

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.content.Context

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.hushed.R
import com.example.hushed.database.DataSource


class SelectedConversationActivity : AppCompatActivity() {
    private var date = Date()
    private var partnerId: String = ""
    private var partnerName: String = ""
    private val formatter = SimpleDateFormat("MM/dd/yy HH:mm:ss:SSS a")
    private lateinit var selectedConversationRecyclerAdapter: SelectedConversationRecyclerAdapter

    private val db = FirebaseFirestore.getInstance()
        .collection("db")
    private val nicknames = FirebaseFirestore.getInstance()
        .collection("nicknames")

    private var conversationUpdatedCallback = Runnable {
        selectedConversationRecyclerAdapter.notifyDataSetChanged()
        messageList.scrollToPosition(selectedConversationRecyclerAdapter.itemCount - 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val actionBar = supportActionBar

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        initRecyclerView()

        partnerId = intent.getStringExtra(ID) ?: ""
        partnerName = intent.getStringExtra(NAME) ?: ""

        DataSource.setViewingConversation(partnerId)

        actionBar!!.title = if (partnerName.isBlank()) "[NO PARTNER]" else partnerName
        initDataSet()

        btnSend.setOnClickListener {
            val timestamp: String = formatter.format(date)

            when {
                txtMessage.text.isNullOrBlank() -> {
                    Toast.makeText(
                        this@SelectedConversationActivity,
                        "Message cannot be blank",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("Message", "Blank message entered!")
                }
                partnerId.isNullOrBlank() -> {
                    Toast.makeText(this, "Sender cannot be blank", Toast.LENGTH_LONG).show()
                    Log.i("Message", "Blank sender entered!")
                }
                else -> sendMessage(txtMessage.text.toString(), timestamp)
            }

            txtMessage.text.clear()
        }
    }

    override fun onResume() {
        super.onResume()
        DataSource.addOnConversationUpdated(conversationUpdatedCallback)
    }

    override fun onPause() {
        super.onPause()
        DataSource.removeOnConversationUpdated(conversationUpdatedCallback)
    }

    private fun sendMessage(msg: String, timestamp: String) {
        val prefFile = getSharedPreferences("SplashActivityPrefsFile", 0)
        val prefs = getSharedPreferences("DeviceKeys", Context.MODE_PRIVATE)

        val privateKey = Keygen.stringToBytes(prefs.getString("privateKey", "NO_KEY"))
        val enc = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val messageInBytes = txtMessage.text.toString().toByteArray()

        val senderName: String = DataSource.getDeviceID(prefFile)
        var convoList = DataSource.getConversationList()
        var index = convoList.indexOfFirst { message -> message.sender == partnerId }

        var message = Messages(
            sender = partnerId,
            message = msg,
            timestamp = timestamp
        )

        selectedConversationRecyclerAdapter.appendMessage(msg, senderName, timestamp)

        if (index >= 0) {
            convoList.removeAt(index)
        }

        convoList.add(message)
        convoList.sortWith(Messages.comparator)

        DataSource.saveTo(getSharedPreferences("DataSource", Context.MODE_PRIVATE))
        messageList.scrollToPosition(selectedConversationRecyclerAdapter.itemCount - 1)

        nicknames.document(partnerName).get()
            .addOnSuccessListener { doc ->
                val partnerPublicKey = Keygen.stringToBytes(doc.get("publicKey") as String)
                val sharedKey = Keygen.generateSharedKey(privateKey, partnerPublicKey)
                val aesSharedKey = EncDec.deriveCipherKey(sharedKey)

                enc.init(Cipher.ENCRYPT_MODE, aesSharedKey)

                val encryptedMessageBytes = EncDec.encrypt(enc, messageInBytes)
                val stringifiedEncMessage = Keygen.byteToString(encryptedMessageBytes)

                db.document(partnerId)
                    .set(
                        hashMapOf(DataSource.getDeviceID(prefFile) to hashMapOf(timestamp to stringifiedEncMessage)),
                        SetOptions.merge()
                    )
                    .addOnSuccessListener { Log.d("Firebase", "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e ->
                        Log.w("Firebase", "Error writing document", e)
                    }
            }
            .addOnFailureListener {  }
    }

    private fun initDataSet() {
        var conversations = DataSource.getConversations()
        var convo = conversations[partnerId] ?: ArrayList()

        if (!conversations.containsKey(partnerId)) {
            conversations[partnerId] = convo
        }

        selectedConversationRecyclerAdapter.setMessages(convo)
        messageList.scrollToPosition(selectedConversationRecyclerAdapter.itemCount - 1)
    }

    private fun initRecyclerView() {
        messageList.apply {
            layoutManager = LinearLayoutManager(this@SelectedConversationActivity).apply {
                stackFromEnd = true
            }

            selectedConversationRecyclerAdapter = SelectedConversationRecyclerAdapter(context)
            adapter = selectedConversationRecyclerAdapter
        }
    }
}
