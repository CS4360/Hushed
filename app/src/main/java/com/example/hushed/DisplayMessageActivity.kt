package com.example.hushed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_display_message.*
import kotlinx.android.synthetic.main.activity_splash.*

class DisplayMessageActivity : AppCompatActivity() {

    var messageText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        val intentThatStartedThisActivity = intent

        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            var msg = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT)
            texView.text = ""
        }

        send_button.setOnClickListener {
            Log.i("tag", "Click: send_button Button")
            if(editText.text.isNullOrBlank()) {
                Toast.makeText(this@DisplayMessageActivity, "Message cannot be blank", Toast.LENGTH_LONG).show()
                Log.i("tag", "Blank message entered")
            } else {
                messageText = editText.text.toString()
                Log.i("tag", "$messageText")
                texView.text = messageText
            }

            editText.text.clear()

        }
    }
}