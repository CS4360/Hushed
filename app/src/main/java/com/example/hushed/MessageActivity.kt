package com.example.hushed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.activity_home_messages.*

const val MESSAGE = "com.example.hushed.MESSAGE"
const val SENDER = "com.example.hushed.SENDER"

// Suggestion from jon: Rename this type "ConversationSelectActivity"
// Naming things is hard, but that better describes what this activity is for
class MessageActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_messages)

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

    private fun initRecyclerView(){
        recyclerViewHome.apply {
            layoutManager = LinearLayoutManager(this@MessageActivity)
            messageAdapter = MessageRecyclerAdapter {message: Messages -> messageClicked(message)}
            adapter = messageAdapter
        }
    }

    private fun messageClicked(msg: Messages) {
        val intent = Intent(this, DisplayMessageActivity::class.java)
        // note from jon: removed the 'Message' extra
        // only need to know who the conversation is supposed to be with
        // we can retrieve the messages from the local database (DataSource)
        intent.putExtra(SENDER, msg.sender)
        startActivity(intent)
    }
}
