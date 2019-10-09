package com.example.hushed

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.activity_message_sent.view.*
import kotlinx.android.synthetic.main.activity_messaged_received.view.*

private var receivedMessage: Boolean = false

class DisplayRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages: MutableList<Messages> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (receivedMessage) {
            ReceivedViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.activity_messaged_received, parent, false)   // Received Message Bubble
            )
        } else {
            MyMessageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.activity_message_sent, parent, false) // Sent Message Bubble
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReceivedViewHolder ->
                holder.bind(messages[position])
            is MyMessageViewHolder ->
                holder.bind(messages[position])
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun submitList(msg: String, sender: String, received: Boolean) {
        messages.add(Messages(
            sender = sender,
            message = msg
        ))

        receivedMessage = received
        notifyDataSetChanged()
    }

    fun sentList(msg: String, sender: String, received: Boolean) {
        messages.add(Messages(
            sender = sender,
            message = msg
        ))
        receivedMessage = received
        Log.i("tag", "In sentList method")
        notifyDataSetChanged()
    }

    class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(msg: Messages) {
            // just display the other message (received)
            Log.i("tag", "Received message")
            itemView.txtOtherMessage.text = msg.message
        }
    }

    class MyMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(msg: Messages) {
            Log.i("tag", "Sent message")
            itemView.txtMyMessage.text = msg.message
        }
    }
}