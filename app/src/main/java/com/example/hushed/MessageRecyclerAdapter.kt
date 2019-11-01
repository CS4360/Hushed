package com.example.hushed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.recycler_messages.view.*

// Suggestion from jon: Rename this type "ConversationSelectRecyclerAdapter"
// Naming things is hard, but that better describes what this adapter is for
class MessageRecyclerAdapter(val clickListener: (Messages) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var messages: List<Messages> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_messages, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MessageViewHolder -> {
                holder.bind(messages.get(position), clickListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun submitList(msg: List<Messages>) {
        messages = msg
    }

    class MessageViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(msg: Messages, clickListener: (Messages) -> Unit) {
            itemView.message_sender.text = msg.sender
            itemView.message_text.text = msg.message
            itemView.setOnClickListener{clickListener(msg)}

            DataSource.nameForId(msg.sender) { name ->
                if (name != DataSource.NO_NAME) {
                    itemView.message_sender.text = name
                }
            }
        }
    }

}