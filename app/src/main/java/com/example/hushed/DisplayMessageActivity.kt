package com.example.hushed

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_display_message.*

class DisplayMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        val intentThatStartedThisActivity = intent

        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            var msg = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT)
            texView.text = msg
        }
    }
}