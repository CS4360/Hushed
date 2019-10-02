/*
    This is where we get the data from the the server
*/
package com.example.hushed

import com.example.hushed.models.Messages

class DataSource {

    companion object {
        private var list = ArrayList<Messages>()
        private var deviceID = ""

        fun getDeviceID(): String {
            return deviceID
        }

        fun setDeviceID(address: String) {
            deviceID = address
        }

        fun getDataSet(): ArrayList<Messages> {
            return list
        }

        fun setDataSet(messages: ArrayList<Messages>) {
            list = messages
        }
    }
}
