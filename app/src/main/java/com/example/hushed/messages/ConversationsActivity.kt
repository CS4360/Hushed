package com.example.hushed.messages

import android.util.Log
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.content.Context

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hushed.R
import com.example.hushed.contacts.ContactsActivity
import com.example.hushed.contacts.NicknameActivity
import kotlinx.android.synthetic.main.activity_home_messages.*

import com.example.hushed.crypto.EncDec
import com.example.hushed.crypto.Keygen
import com.example.hushed.database.DataSource

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

import kotlin.collections.HashMap
import kotlin.collections.ArrayList


const val ID = "com.example.hushed.messages.ID"
const val NAME = "com.example.hushed.messages.NAME"


class ConversationsActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
        .collection("db")
    private val nicknames = FirebaseFirestore.getInstance()
        .collection("nicknames")

    private lateinit var messageAdapter: ConversationsRecyclerAdapter
    private lateinit var listener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefFile = getSharedPreferences("SplashActivityPrefsFile", 0)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_messages)

        listener = db.document(DataSource.getDeviceID(prefFile))
            .addSnapshotListener {  doc, e ->
                if (e != null) {
                    Log.w("Firebase", "Error listening to document", e)
                    return@addSnapshotListener
                }

                if (doc == null) {
                    Log.w("Firebase", "Snapshot missing")
                    return@addSnapshotListener
                }

                val source = if (doc.metadata.hasPendingWrites()) "Local" else "Server"

                if (source == "Server") {
                    onLoadedDocument(doc)
                }
                else {
                    DataSource.saveTo(getSharedPreferences("DataSource", Context.MODE_PRIVATE))
                    messageAdapter.notifyDataSetChanged()
                }

            }

        initRecyclerView()
        setConversationList()

        new_message.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener.remove()
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setConversationList() {
        val conversation = DataSource.getConversationList()
        messageAdapter.setMessageList(conversation)
    }

    private fun initRecyclerView() {
        recyclerViewHome.apply {
            layoutManager = LinearLayoutManager(this@ConversationsActivity).apply {
                reverseLayout = true
                stackFromEnd = true
            }
            messageAdapter =
                ConversationsRecyclerAdapter(context) { message: Messages ->
                    messageClicked(
                        message
                    )
                }
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

    private fun onLoadedDocument(doc: DocumentSnapshot) {
        var conversationChanged = false
        var conversationList = DataSource.getConversationList()
        var conversationMap = DataSource.getConversations()

        val prefFile = getSharedPreferences("SplashActivityPrefsFile", 0)
        val prefs = getSharedPreferences("DeviceKeys", Context.MODE_PRIVATE)
        val privateKey = Keygen.stringToBytes(prefs.getString("privateKey", "NO_KEY"))
        val dec = Cipher.getInstance("AES/CBC/PKCS5Padding")

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

            conversationChanged = key == DataSource.getViewingConversation()

            DataSource.nameForId(partnerId) { partnerNickname ->
                nicknames.document(partnerNickname).get()
                    .addOnSuccessListener { doc ->
                        Log.i("Database", "New messages for ${partnerId}!")

                        for ((timestamp, message) in newMessages) {
                            val encryptedMessageBytes = Keygen.stringToBytes(message as String)
                            val ivParamSpec = IvParameterSpec(encryptedMessageBytes, 0, dec.blockSize)

                            val partnerPublicKey = Keygen.stringToBytes(doc.get("publicKey") as String)
                            val sharedKey = Keygen.generateSharedKey(privateKey, partnerPublicKey)
                            val aesSharedKey = EncDec.deriveCipherKey(sharedKey)

                            dec.init(Cipher.DECRYPT_MODE, aesSharedKey, ivParamSpec)

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

                        var index =
                            conversationList.indexOfFirst { message -> message.sender == partnerId }

                        if (index >= 0) {
                            conversationList.removeAt(index)
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
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(applicationContext, NicknameActivity::class.java)
        startActivity(intent)

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
