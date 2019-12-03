package com.example.hushed.contacts

import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_contacts.*

import android.util.Log
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.view.View

import com.example.hushed.*
import com.example.hushed.messages.ID
import com.example.hushed.messages.NAME
import com.example.hushed.database.DataSource
import com.example.hushed.messages.SelectedConversationActivity


class ContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val actionBar = supportActionBar
        actionBar!!.title = "Send To"

        enter_button.setOnClickListener {
            if (enter_contact.text.isNullOrBlank()) {
                Toast.makeText(this, "Contact cannot be blank", Toast.LENGTH_LONG).show()
                Log.i("Message", "Blank message entered")
            }
            else {
                checkForContact(enter_contact.text.toString())
            }
        }
    }

    private fun checkForContact(contact: String) {
        DataSource.idForName(contact) { id ->
            if (id != DataSource.NO_ID) {
                setContact(id, contact)
            }
            else {
                Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show()

            }
        }
        
    }

    private fun setContact(id: String, name: String) {
        val intent = Intent(this, SelectedConversationActivity::class.java)
        intent.putExtra(ID, id)
        intent.putExtra(NAME, name)
        startActivity(intent)
    }
}