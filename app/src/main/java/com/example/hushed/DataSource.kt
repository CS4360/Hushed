/*
    This is where we get the data from the the server
*/
package com.example.hushed

import android.content.SharedPreferences
import android.util.Log
import com.example.hushed.models.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlin.collections.ArrayList

class DataSource {
    companion object {

        private val nicknames = FirebaseFirestore.getInstance()
            .collection("nicknames")


        // to hold the last message of various users, displayed when selecting a conversation.
        private var conversationList = ArrayList<Messages>()

        // map of UserID to message to display when selecting a conversation
        // keys are UserIDs of the partner user that a conversation is held with
        // values are the last message received by or sent to that partner user
        private var conversations = HashMap<String, ArrayList<Messages>>()

        //  This holds this user's UUID
        private var deviceID = ""

        // This holds the id of the conversation we are currently viewing
        private var viewingConversation = ""

        // callbacks to run when conversations have updated
        private var onConversationUpdated = ArrayList<Runnable>()

        fun getViewingConversation(): String {
            return viewingConversation
        }

        fun setViewingConversation(id: String) {
            viewingConversation = id;
        }

        // register callback for when conversations have updated
        fun addOnConversationUpdated(callback: Runnable) {
            onConversationUpdated.add(callback)
        }

        // unregister callback for when conversations have updated
        fun removeOnConversationUpdated(callback: Runnable) {
            onConversationUpdated.remove(callback)
        }

        // Invoke all registered callbacks for when conversations have updated
        fun conversationUpdated() {
            for (callback in onConversationUpdated) {
                try {
                    callback.run()
                } catch (e: Exception) {
                    Log.e("Callback", "Error in callback", e);
                }
            }
        }

        fun getConversations(): HashMap<String, ArrayList<Messages>> {
            return conversations
        }

        // Right now, no setters for conversationList/conversations
        // as those should mimic the database, and if elements are received,
        // they can just be added to the existing data

        fun getConversationList(): ArrayList<Messages> {
            return conversationList
        }

        fun getDeviceID(): String {
            return deviceID
        }

        fun setDeviceID(address: String) {
            deviceID = address
        }

        const val NO_ID = "[NO_ID]"
        fun idForName(name: String, callback: (String) -> Unit) {
            nicknames.document(name)
                .get()
                .addOnSuccessListener { doc ->

                    if (doc.data.orEmpty().isNotEmpty()) {
                        var id = doc.data!!["id"]
                        if (id is String) {
                            callback(id)
                        } else {
                            callback(NO_ID)
                        }
                    } else {
                        callback(NO_ID)
                    }
                }
        }

        const val NO_NAME = "[NO_NAME]"
        fun nameForId(id: String, callback: (String) -> Unit) {
            nicknames.whereEqualTo("id", id)
                .get()
                .addOnSuccessListener { query ->
                    if (!query.isEmpty) {
                        // id of document is nickname of user
                        callback(query.documents[0].id)
                    } else {
                        callback(NO_NAME)
                    }
                }
        }


        fun saveTo(prefs: SharedPreferences) {
            var map = JsonObject()

            Log.i("Test", "Saving " + conversations.size + " Conversations")
            for ((key, value) in conversations) {
                var convo = JsonArray()

                for (i in value.indices) {
                    var message = value[i]
                    var msg = JsonObject()

                    msg.addProperty("sender", message.sender)
                    msg.addProperty("message", message.message)
                    msg.addProperty("timestamp", message.timestamp)

                    convo.add(msg)
                }
                map.add(key, convo)
            }

            prefs.edit()?.putString("conversations", map.toString())?.apply()
            Log.i("Test", "Saved")
        }

        fun loadFrom(prefs: SharedPreferences) {
            var json = prefs.getString("conversations", "{}")

            var map = JsonParser().parse(json) as JsonObject

            Log.i("Test", "Loaded " + map.size() + " Conversations")

            conversations.clear()
            conversationList.clear()

            for ((key, value) in map.entrySet()) {
                var convo = ArrayList<Messages>()
                conversations[key] = convo

                if (value is JsonArray) {
                    for (i in 0 until value.size()) {
                        var msg = value.get(i)
                        if (msg is JsonObject) {


                            var message = Messages(
                                sender = msg.get("sender").asString,
                                timestamp = msg.get("timestamp").asString,
                                message = msg.get("message").asString
                            )

                            convo.add(message)
                        }
                    }

                    convo.sortWith(Messages.comparator)

                    if(convo.size > 0) {
                        var lastMessage = convo[convo.size - 1]
                        var msg = Messages(
                            timestamp = lastMessage.timestamp,
                            message = lastMessage.message,
                            sender = key
                        )
                        conversationList.add(msg)
                    }
                }
                conversationList.sortWith(Messages.comparator)
            }


        }

    }
}
