package com.example.hushed

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
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
        val senderName = intent.getStringExtra(SENDER)
        actionBar!!.title = senderName

        initRecyclerView()
        val intentMsg: String? = intent.getStringExtra(MESSAGE)
        if(intentMsg != null) {
            addDataSet()
        }

        btnSend.setOnClickListener{
            Log.i("tag", "Click: send_button Button")
            if(txtMessage.text.isNullOrBlank()) {
                Toast.makeText(this@DisplayMessageActivity, "Message cannot be blank", Toast.LENGTH_LONG).show()
                Log.i("tag", "Blank message entered")
            }
            else {
                sentDataSet(txtMessage.text.toString())

                db.document(DataSource.getDeviceID()).update(intent.getStringExtra(SENDER),
                    FieldValue.arrayUnion(hashMapOf("sent" to txtMessage.text.toString())))
                    .addOnSuccessListener { Log.d("Firebase", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener {
                        db.document(DataSource.getDeviceID())
                            .set(hashMapOf(intent.getStringExtra(SENDER) to FieldValue.arrayUnion(hashMapOf("sent" to txtMessage.text.toString()))),
                            SetOptions.merge())
                            .addOnSuccessListener { Log.d("Firebase", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("Firebase", "Error writing document", e)
                            }
                    }
            }

            txtMessage.text.clear()
        }
    }

    private fun sentDataSet(msg: String) {
        val actionBar = supportActionBar
        val senderName: String = intent.getStringExtra(SENDER)
        actionBar!!.title = senderName

        displayAdapter.sentList(msg, senderName, false)
        messageList.scrollToPosition(displayAdapter.itemCount - 1)
    }

    private fun addDataSet() {
        val actionBar = supportActionBar
        val senderName: String? = intent.getStringExtra(SENDER)
        actionBar!!.title = senderName

        val intentMsg: String? = intent.getStringExtra(MESSAGE)
        displayAdapter.submitList(intentMsg.toString(), senderName.toString(), true)
        messageList.scrollToPosition(displayAdapter.itemCount - 1)
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
