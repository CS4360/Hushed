package com.example.hushed

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message_chat.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class DisplayMessageActivity : AppCompatActivity() {

    private lateinit var displayAdapter: DisplayRecyclerAdapter

    private val db = FirebaseFirestore.getInstance()
        .collection("db")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val actionBar = supportActionBar
        val senderName = intent.getStringExtra(EXTRA_TEXT)
        actionBar!!.title = senderName

        initRecyclerView()
        addDataSet()

        btnSend.setOnClickListener{
            Log.i("tag", "Click: send_button Button")
            if(txtMessage.text.isNullOrBlank()) {
                Toast.makeText(this@DisplayMessageActivity, "Message cannot be blank", Toast.LENGTH_LONG).show()
                Log.i("tag", "Blank message entered")
            }
            else {
                val peer: String? = intent.getStringExtra(EXTRA_TEXT)

                var messageText: HashMap<Any, Any> = hashMapOf(peer.toString() to txtMessage.text.toString())

                sentDataSet(txtMessage.text.toString())

                db.document(DataSource.getDeviceID()).set(messageText, SetOptions.merge())
                    .addOnSuccessListener { Log.d("Firebase", "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w("Firebase", "Error writing document", e) }
            }

            txtMessage.text.clear()
        }
    }

    private fun sentDataSet(msg: String) {
        val actionBar = supportActionBar
        val senderName: String = intent.getStringExtra(EXTRA_TEXT)
        actionBar!!.title = senderName

        displayAdapter.sentList(msg, senderName, false)
    }

    private fun addDataSet() {
        val actionBar = supportActionBar
        val senderName: String? = intent.getStringExtra(EXTRA_TEXT)
        actionBar!!.title = senderName

        val intentMsg: String? = intent.getStringExtra(EXTRA_MESSAGE)
        displayAdapter.submitList(intentMsg.toString(), senderName.toString(), true)
    }

    private fun initRecyclerView() {
        messageList.apply {
            layoutManager = LinearLayoutManager(this@DisplayMessageActivity).apply {
                stackFromEnd = true
            }
            displayAdapter = DisplayRecyclerAdapter()
            adapter = displayAdapter
        }
    }
}
