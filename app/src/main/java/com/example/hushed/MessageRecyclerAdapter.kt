package com.example.hushed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.recycler_messages.view.*

class MessageRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var messages: List<Messages> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_messages, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MessageViewHolder -> {
                holder.bind(messages.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun submitList(msg: List<Messages>) {
        messages = msg
    }

    class MessageViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {

        val msgImage = itemView.msg_image
        val messageText = itemView.message_text

        fun bind(msg: Messages) {
            messageText.setText(msg.message)
        }
    }

}