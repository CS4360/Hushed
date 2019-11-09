package com.example.hushed

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hushed.models.Messages
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home_messages.*

const val ID = "com.example.hushed.ID"
const val NAME = "com.example.hushed.NAME"

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

    private fun initRecyclerView() {
        recyclerViewHome.apply {
            layoutManager = LinearLayoutManager(this@MessageActivity)
            messageAdapter = MessageRecyclerAdapter(context) {message: Messages -> messageClicked(message) }
            adapter = messageAdapter
        }
    }

    private fun messageClicked(msg: Messages) {
        val intent = Intent(this, DisplayMessageActivity::class.java)


        DataSource.nameForId(msg.sender) { name ->
            intent.putExtra(NAME, name)
            intent.putExtra(ID, msg.sender)
            startActivity(intent)
        }
    }
}
