package com.example.hushed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_contacts.*

class ContactsActivity : AppCompatActivity() {
    private val nicknames = FirebaseFirestore.getInstance()
        .collection("nicknames")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val actionBar = supportActionBar
        actionBar!!.title = "Send To"

        enter_button.setOnClickListener {
            Log.i("tag", "Click: enter_button Button")
            if (enter_contact.text.isNullOrBlank()) {
                Toast.makeText(this, "Contact cannot be blank", Toast.LENGTH_LONG).show()
                Log.i("tag", "Blank message entered")
            } else {
                checkForContact(enter_contact.text.toString())
            }
        }
    }

    private fun checkForContact(contact: String) {
        DataSource.idForName(contact) {id ->
            if (id != DataSource.NO_ID) {
                setContact(id, contact)
            } else {
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