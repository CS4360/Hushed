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

class MessageActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_messages)

        initRecyclerView()
        addDataSet()

        new_message.setOnClickListener {
            Log.i("tag", "Click: new_message Button")
            val intent = Intent(this, DisplayMessageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addDataSet() {
        val data = DataSource.getDataSet()
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
        intent.putExtra(MESSAGE, msg.message)
        intent.putExtra(SENDER, msg.sender)
        startActivity(intent)
    }
}
