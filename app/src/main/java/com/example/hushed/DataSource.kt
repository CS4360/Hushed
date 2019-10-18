/*
    This is where we get the data from the the server
*/
package com.example.hushed

import com.example.hushed.models.Messages
import kotlin.collections.ArrayList

class DataSource {

    // Note from jon: companion objects are like static objects.
    // This is essentially a static/global database, which is fine
    // so long as you understand any part of the app can access and modify this data.
    companion object {
        // to hold the last message of various users, displayed when selecting a conversation.
        private var conversationList = ArrayList<Messages>()

        // map of UserID to message to display when selecting a conversation
        // keys are UserIDs of the partner user that a conversation is held with
        // values are the last message received by or sent to that partner user
        private var conversations = HashMap<String, ArrayList<Messages>>()

        //  This holds this user's UUID
        private var deviceID = ""

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


    }
}
