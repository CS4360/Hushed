package com.example.hushed

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message_chat.*

class DisplayMessageActivity : AppCompatActivity() {

    var messageText: String = ""

    private lateinit var displayAdapter: DisplayRecyclerAdapter

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
            } else {
                messageText = txtMessage.text.toString()
                Log.i("tag", "$messageText")
            }
            sentDataSet(messageText)
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
//        val data = DataSource.getDataSet()
        val actionBar = supportActionBar
        val senderName: String = intent.getStringExtra(EXTRA_TEXT)
        actionBar!!.title = senderName

        val intentMsg: String = intent.getStringExtra(EXTRA_MESSAGE)
        displayAdapter.submitList(intentMsg, senderName, true)
    }

    private fun initRecyclerView() {
        messageList.apply {
            layoutManager = LinearLayoutManager(this@DisplayMessageActivity)
            displayAdapter = DisplayRecyclerAdapter()
            adapter = displayAdapter
        }
    }
}