package com.example.hushed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
        addDataSet()
    }

    private fun addDataSet() {
        val data = DataSource.createDataSet()
        messageAdapter.submitList(data)
    }

    private fun initRecyclerView(){
        recyclerViewHome.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            messageAdapter = MessageRecyclerAdapter {message: Messages -> messageClicked(message)}
            adapter = messageAdapter
        }
    }

    private fun messageClicked(msg: Messages) {
        Toast.makeText(this, msg.message, Toast.LENGTH_LONG).show()

        val intent = Intent(this, DisplayMessageActivity::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, msg.message)
        startActivity(intent)
    }
}
