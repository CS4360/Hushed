package com.example.hushed

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.hushed.models.Messages
import kotlinx.android.synthetic.main.recycler_messages.view.*

// Suggestion from jon: Rename this type "ConversationsRecyclerAdapter"
// Naming things is hard, but that better describes what this adapter is for
class ConversationsRecyclerAdapter(val context: Context, val clickListener: (Messages) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var messages: MutableList<Messages> = ArrayList()

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

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun removeItem(position: Int) {
        messages.removeAt(position)
        notifyDataSetChanged()
    }

    fun submitList(msg: MutableList<Messages>) {
        messages = msg
    }

    inner class MessageViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(msg: Messages, clickListener: (Messages) -> Unit) {
            itemView.message_sender.text = msg.sender
            itemView.message_text.text = msg.message
            itemView.setOnClickListener{clickListener(msg)}
            // just holding down on the message view
            itemView.setOnLongClickListener {

                val preferences = context.getSharedPreferences("DataSource", Context.MODE_PRIVATE)
                val builder = AlertDialog.Builder(context)

                builder.setTitle("Delete Conversation")
                builder.setMessage("Are you sure you want to delete conversation?")
                builder.setPositiveButton("YES") { _, _ ->
                    removeItem(layoutPosition)
                    DataSource.deleteConversationsFrom(preferences, msg.sender)
                    Toast.makeText(context, "Conversation deleted!",Toast.LENGTH_LONG).show()
                }

                builder.setNegativeButton("No"){
                    _, _ ->
                    Toast.makeText(context, "Conversation not deleted",Toast.LENGTH_LONG).show()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                true
            }

            DataSource.nameForId(msg.sender) { name ->
                if (name != DataSource.NO_NAME) {
                    itemView.message_sender.text = name
                }
            }

            if (DataSource.idToNicknameCache.containsKey(msg.sender)) {
                itemView.message_sender.text = DataSource.idToNicknameCache[msg.sender]
            }
        }
    }

}