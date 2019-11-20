package com.example.hushed.messages

import androidx.recyclerview.widget.RecyclerView

import java.util.Date
import java.text.SimpleDateFormat

import kotlin.collections.ArrayList

import android.view.View
import android.widget.Toast
import android.view.ViewGroup
import android.widget.TextView
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater

import com.example.hushed.R
import com.example.hushed.database.DataSource


class SelectedConversationRecyclerAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var date = Date()
    private var messages: MutableList<Messages> = ArrayList()
    private val timeFormatter = SimpleDateFormat("MM/dd/yy HH:mm a")

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

    fun setMessages(submittedMessages: ArrayList<Messages>) {
        messages = submittedMessages
        notifyDataSetChanged()
    }

    fun appendMessage(msg: String, sender: String, time: String) {
        messages.add(
            Messages(
                sender = sender,
                message = msg,
                timestamp = time
            )
        )

        notifyDataSetChanged()
    }

    fun removeMessage(position: Int) {
        messages.removeAt(position)
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receivedMessage: TextView = itemView.findViewById(R.id.txtOtherMessage)
        private val receivedMessageTime: TextView = itemView.findViewById(R.id.txtOtherMessageTime)
        private val sentMessage: TextView = itemView.findViewById(R.id.txtMyMessage)
        private val sentMessageTime: TextView = itemView.findViewById(R.id.txtMyMessageTime)

        fun bind(msg: Messages) {
            val prefFile = context.getSharedPreferences("SplashActivityPrefsFile", 0)
            val preferences = context.getSharedPreferences("DataSource", Context.MODE_PRIVATE)
            var ownId = DataSource.getDeviceID(prefFile)

            itemView.setOnLongClickListener {
                val builder = AlertDialog.Builder(context)
                val dialog: AlertDialog

                builder.setTitle("Delete Message")
                builder.setMessage("Are you sure you want to delete message?")
                builder.setPositiveButton("YES") { _, _ ->
                    removeMessage(layoutPosition)
                    DataSource.deleteMessageFrom(preferences, msg.sender, msg.message, msg.timestamp)
                    Toast.makeText(context, "Message deleted!", Toast.LENGTH_LONG).show()
                }

                builder.setNegativeButton("No"){
                        _, _ ->
                    Toast.makeText(context, "Message not deleted", Toast.LENGTH_LONG).show()
                }

                dialog = builder.create()
                dialog.show()
                true
            }

            if (msg.sender == ownId) {
                sentMessage.text = msg.message
                sentMessage.visibility = View.VISIBLE
                sentMessageTime.text = timeFormatter.format(date)
                sentMessageTime.visibility = View.VISIBLE

                receivedMessage.text = ""
                receivedMessage.visibility = View.GONE
                receivedMessageTime.text = ""
                receivedMessageTime.visibility = View.GONE

            }
            else {
                receivedMessage.text = msg.message
                receivedMessage.visibility = View.VISIBLE
                receivedMessageTime.text = timeFormatter.format(date)
                receivedMessageTime.visibility = View.VISIBLE

                sentMessage.text = ""
                sentMessage.visibility = View.GONE
                sentMessageTime.text = ""
                sentMessageTime.visibility = View.GONE
            }
        }
    }
}