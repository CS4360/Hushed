/*
    This is where we get the data from the the server
*/
package com.example.hushed

import com.example.hushed.models.Messages

class DataSource {

    companion object {
        private var list = ArrayList<Messages>()

        fun getDataSet(): ArrayList<Messages> {
            return list
        }

        fun setDataSet(messages: ArrayList<Messages>) {
            list = messages
        }
    }
}
