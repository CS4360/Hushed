package com.example.hushed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_contacts.*

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val actionBar = supportActionBar
        actionBar!!.title = "Send To"

        enter_button.setOnClickListener {
            Log.i("tag", "Click: enter_button Button")
            if(enter_contact.text.isNullOrBlank()) {
                Toast.makeText(this, "Contact cannot be blank", Toast.LENGTH_LONG).show()
                Log.i("tag", "Blank message entered")
            }
            else {
                setContact(enter_contact.text.toString())
            }
        }
    }

    private fun setContact(contact: String) {
        val intent = Intent(this, DisplayMessageActivity::class.java)
        intent.putExtra(SENDER, contact)
        startActivity(intent)
    }
}