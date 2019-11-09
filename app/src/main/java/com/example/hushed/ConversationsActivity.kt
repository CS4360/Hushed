package com.example.hushed

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

class MessageActivity : AppCompatActivity() {
    private val nicknames = FirebaseFirestore.getInstance()
        .collection("nicknames")
    private lateinit var messageAdapter: ConversationsRecyclerAdapter

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
}
