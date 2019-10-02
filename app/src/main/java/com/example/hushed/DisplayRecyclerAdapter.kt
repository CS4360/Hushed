package com.example.hushed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.activity_message_sent.view.*
//import kotlinx.android.synthetic.main.activity_messaged_received.view.*

private const val VIEW_TYPE_SENT = 1
private const val VIEW_TYPE_RECIEVED = 2

class DisplayRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private var messages: List<Messages> = ArrayList()
    private var message: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DisplayViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_message_sent, parent, false) // Sent Message Bubble
//            LayoutInflater.from(parent.context).inflate(R.layout.activity_messaged_received, parent, false)   // Sent Received Bubble
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is DisplayViewHolder ->
                holder.bind(message[position])
        }
    }

    override fun getItemCount(): Int {
        return message.size
    }

//    fun submitList(msg: List<Messages>) {
//        messages = msg
//    }

    fun sentList(msg: String) {
        message.add(msg)
    }

    fun submitList(msg: String) {
        message.add(msg)
    }

    class DisplayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(msg: String) {
            // just display the other message (received)
            itemView.txtMyMessage.text = msg

        }
    }
}