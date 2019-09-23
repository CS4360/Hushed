package com.example.hushed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.activity_home_messages.*

class MessageActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_messages)

        initRecyclerView()
        addDataSet()
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
        intent.putExtra(Intent.EXTRA_TEXT, msg.message)
        startActivity(intent)
    }
}
