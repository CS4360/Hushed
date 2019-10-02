package com.example.hushed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.activity_message_sent.view.*
import kotlinx.android.synthetic.main.activity_messaged_received.view.*

class DisplayRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages: MutableList<Messages> = ArrayList()
//    private var message: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DisplayViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.activity_message_sent, parent, false) // Sent Message Bubble
            LayoutInflater.from(parent.context).inflate(R.layout.activity_messaged_received, parent, false)   // Sent Received Bubble
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is DisplayViewHolder ->
                holder.bind(messages[position])
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun submitList(msg: String, sender: String) {
        messages.add(Messages(
            sender = sender,
            message = msg
        ))
    }

    class DisplayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(msg: Messages) {
            // just display the other message (received)
            itemView.txtOtherMessage.text = msg.message

        }
    }
}