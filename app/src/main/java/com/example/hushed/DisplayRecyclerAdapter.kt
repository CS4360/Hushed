package com.example.hushed

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hushed.models.Messages

class DisplayRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages: MutableList<Messages> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MessageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false)
            )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageViewHolder -> holder.bind(messages[position])
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    // note from jon: Changed this method to to take a whole list
    // and make the adapter use that list
    fun submitList(msgs: ArrayList<Messages>) {
        messages = msgs
        notifyDataSetChanged()
    }

    // note from jon: renamed this from 'sendList', this name is a bit more clear
    fun addMessage(msg: String, sender: String) {
        messages.add(Messages(
            sender = sender,
            message = msg
        ))
        Log.i("tag", "In addMessage method")
        notifyDataSetChanged()
    }

    // Note from jon:
    // I combined the two previous viewholders together, similarly to combining their layouts.
    // Since we don't know ahead of time what messages are sent or received,
    // and the viewholder will get reused to display potentially to display the other kind of message
    // it is way easier to allow for both messages, and enable/disable internal views as needed
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Cache references to child views on construction:
        private val receivedMessage: TextView = itemView.findViewById(R.id.txtOtherMessage)
        private val receivedMessageTime: TextView = itemView.findViewById(R.id.txtOtherMessageTime)
        private val sentMessage: TextView = itemView.findViewById(R.id.txtMyMessage)
        private val sentMessageTime: TextView = itemView.findViewById(R.id.txtMyMessageTime)

        fun bind(msg: Messages) {
            var ownId = DataSource.getDeviceID()

            // figure out if we are displaying a sent or received message by the id of the sender
            if (msg.sender.equals(ownId)) {
                Log.i("tag", "Sent message")
                // if we are displaying a 'sent' message, make sure we show only 'sent' features.
                sentMessage.text = msg.message
                sentMessage.visibility = View.VISIBLE
                sentMessageTime.text = "--/--/--"
                sentMessageTime.visibility = View.VISIBLE
                // hide all received views
                receivedMessage.text = ""
                receivedMessage.visibility = View.GONE
                receivedMessageTime.text = ""
                receivedMessageTime.visibility = View.GONE

            } else {
                Log.i("tag", "Received message")
                // if we are displaying a 'received' message, make sure we show only 'received' features.
                receivedMessage.text = msg.message
                receivedMessage.visibility = View.VISIBLE
                receivedMessageTime.text = "--/--/--"
                receivedMessageTime.visibility = View.VISIBLE
                // hide all 'sent' views
                sentMessage.text = ""
                sentMessage.visibility = View.GONE
                sentMessageTime.text = ""
                sentMessageTime.visibility = View.GONE
            }
        }
    }
}