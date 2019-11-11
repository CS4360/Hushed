/*
    This is where we get the data from the the server
*/
package com.example.hushed

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.hushed.models.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
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
            viewingConversation = id
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
                    Log.e("Callback", "Error in callback", e)
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
                        val id = doc.data!!["id"]
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
            val map = JsonObject()

            Log.i("Test", "Saving " + conversations.size + " Conversations")
            for ((key, value) in conversations) {
                val convo = JsonArray()

                for (i in value.indices) {
                    val message = value[i]
                    val msg = JsonObject()

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
                val convo = ArrayList<Messages>()
                conversations[key] = convo

                if (value is JsonArray) {
                    for (i in 0 until value.size()) {
                        val msg = value.get(i)
                        if (msg is JsonObject) {


                            val message = Messages(
                                sender = msg.get("sender").asString,
                                timestamp = msg.get("timestamp").asString,
                                message = msg.get("message").asString
                            )

                            convo.add(message)
                        }
                    }

                    convo.sortWith(Messages.comparator)

                    if (convo.size > 0) {
                        val lastMessage = convo[convo.size - 1]
                        val msg = Messages(
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


        fun deleteConversationsFrom(prefs: SharedPreferences, id: String) {
            val json = prefs.getString("conversations", "{}")
            var map = JsonParser().parse(json) as JsonObject

            // remove both from preferences and conversations
            map.remove(id)
            prefs.edit()?.putString("conversations", map.toString())?.apply()
            conversations.remove(id)
        }

        fun deleteMessageFrom(prefs: SharedPreferences, id: String, message: String, timestamp: String) {
            val json = prefs.getString("conversations", "{}")
            var map = JsonParser().parse(json) as JsonObject
            Log.i("deleteMessageFrom", "map before: $map")
            var count = 0

            for ((key, value) in map.entrySet()) {

                if(value is JsonArray && value.size() != 0) {
                    Log.i("deleteMessageFrom", "value size: " + value.size())
                    while(count < value.size()) {
                        val msg = value.get(count)

                        if(msg is JsonObject) {
                            var sender = msg.get("sender").toString()
                            var singleMsg = msg.get("message").toString()
                            if(sender.contains(id)) {
                                if(singleMsg.contains(message)) {
                                    value.remove(count)
                                    count = 0
                                    Log.i("deleteMessageFrom","removed message from value, resetting count")
                                }
                            }
                            count ++
                        }
                    }
                }
                else {
                    Log.i("deleteMessageFrom", "value is not a JsonArray or is empty")
                }
            }
            Log.i("deleteMessageFrom", "map after: $map")
            prefs.edit()?.putString("conversations", map.toString())?.apply()
        }

        fun saveKeys(prefs: SharedPreferences, privKey: String, pubKey: String) {
            prefs.edit()?.putString("publicKey", pubKey)?.apply()
            prefs.edit()?.putString("privateKey", privKey)?.apply()
        }

        fun getPublicKey(prefs: SharedPreferences): String {
            return prefs.getString("publicKey", "NO_KEY").toString()
        }

        fun getPrivateKey(prefs: SharedPreferences): String {
            return prefs.getString("privateKey", "NO_KEY").toString()
        }
    }
}
