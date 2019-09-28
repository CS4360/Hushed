package com.example.hushed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message_chat.*

class DisplayMessageActivity : AppCompatActivity() {

    private lateinit var displayAdapter: DisplayRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        initRecyclerView()
        addDataSet()
    }

    private fun addDataSet() {
        val actionBar = supportActionBar
        actionBar!!.title = intent.getStringExtra(EXTRA_TEXT)
//        val data = DataSource.getDataSet()
        val intentMsg = intent.getStringExtra(EXTRA_MESSAGE)
        displayAdapter.submitList(intentMsg)
    }

    private fun initRecyclerView() {
        messageList.apply {
            layoutManager = LinearLayoutManager(this@DisplayMessageActivity)
            displayAdapter = DisplayRecyclerAdapter()
            adapter = displayAdapter
        }
    }
}