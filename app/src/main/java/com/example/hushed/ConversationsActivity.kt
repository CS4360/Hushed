package com.example.hushed

import android.util.Log
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.content.Context

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home_messages.*

import com.example.hushed.crypto.EncDec
import com.example.hushed.crypto.Keygen
import com.example.hushed.models.Messages

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import java.util.Timer

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

import kotlin.collections.HashMap
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate


const val ID = "com.example.hushed.ID"
const val NAME = "com.example.hushed.NAME"


class ConversationsActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
        .collection("db")
    private val nicknames = FirebaseFirestore.getInstance()
        .collection("nicknames")
    private var timer: Timer = Timer()

    private lateinit var messageAdapter: ConversationsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefFile = getSharedPreferences("SplashActivityPrefsFile", 0)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_messages)

        db.document(DataSource.getDeviceID(prefFile)).get()
            .addOnSuccessListener { doc ->
                onLoadedDocument(doc)

                refreshConversations()
            }
            .addOnFailureListener { e ->
                Log.w(
                    "Firebase",
                    "Error retrieving document from database", e
                )
            }

        initRecyclerView()
        addDataSet()

        new_message.setOnClickListener {
            Log.i("tag", "Click: new_message Button")
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addDataSet() {
        val data = DataSource.getConversationList()
        messageAdapter.submitList(data)
    }

    private fun initRecyclerView() {
        recyclerViewHome.apply {
            layoutManager = LinearLayoutManager(this@ConversationsActivity)
            messageAdapter = ConversationsRecyclerAdapter(context) { message: Messages -> messageClicked(message) }
            adapter = messageAdapter
        }
    }

    private fun messageClicked(msg: Messages) {
        val intent = Intent(this, SelectedConversationActivity::class.java)


        DataSource.nameForId(msg.sender) { name ->
            intent.putExtra(NAME, name)
            intent.putExtra(ID, msg.sender)
            startActivity(intent)
        }
    }

    private fun refreshConversations() {
        val prefFile = getSharedPreferences("SplashActivityPrefsFile", 0)
        // Refreshes conversations every 10 seconds with data from the database
        // We can decrease the interval to see faster responses.
        // Ex. var task = timer.scheduleAtFixedRate(1*1000L, 1 * 1000L)
        timer.scheduleAtFixedRate(10 * 1000L, 10 * 1000L) {


            Log.i("Conversations", "Refreshing conversations")
            db.document(DataSource.getDeviceID(prefFile)).get()
                .addOnSuccessListener(::onLoadedDocument)
        }
    }

    private fun onLoadedDocument(doc: DocumentSnapshot) {
        val prefFile = getSharedPreferences("SplashActivityPrefsFile", 0)

        var conversationChanged = false
        var conversationList = DataSource.getConversationList()
        var conversationMap = DataSource.getConversations()

        val prefs = getSharedPreferences("DeviceKeys", Context.MODE_PRIVATE)
        val privateKey = Keygen.stringToBytes(prefs.getString("privateKey", "NO_KEY"))
        val dec = Cipher.getInstance("AES/CBC/PKCS5Padding")

        Log.i("Database", "Got new data!")

        var id = DataSource.getDeviceID(prefFile)

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

            DataSource.nameForId(partnerId) { partnerNickname ->
                nicknames.document(partnerNickname).get()
                    .addOnSuccessListener { doc ->
                        Log.i("Database", "New messages for ${partnerId}!")
                        for ((timestamp, message) in newMessages) {
                            val encryptedMessageBytes = Keygen.stringToBytes(message as String)
                            val iv = IvParameterSpec(encryptedMessageBytes, 0, dec.getBlockSize())

                            val partnerPublicKey = Keygen.stringToBytes(doc.get("publicKey") as String)
                            val sharedKey = Keygen.generateSharedKey(privateKey, partnerPublicKey)
                            val aesSharedKey = EncDec.deriveCipherKey(sharedKey)
                            dec.init(Cipher.DECRYPT_MODE, aesSharedKey, iv)

                            val decryptedMessageBytes = EncDec.decrypt(dec, encryptedMessageBytes)
                            val decryptedMessage = String(decryptedMessageBytes)

                            conversation.add(
                                Messages(
                                    timestamp = timestamp as String,
                                    sender = partnerId,
                                    message = decryptedMessage
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

                        db.document(id).update(updates)
                        conversationList.sortWith(Messages.comparator)

                        if (conversationChanged) {
                            DataSource.conversationUpdated()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firebase", "Error retrieving data from $partnerNickname: ", e)
                    }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val intent = Intent(applicationContext, NicknameActivity::class.java)
        startActivity(intent)
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
