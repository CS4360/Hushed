package com.example.hushed

import com.example.hushed.models.Messages

class DataSource {

    companion object {

        fun createDataSet(): ArrayList<Messages> {
            val list = ArrayList<Messages>()
            list.add(
                Messages(
                    message = "Hey there!"
                )
            )
            list.add(
                Messages(
                    message = "Please call me back"
                )
            )
            list.add(
                Messages(
                    message = "Just wanted to let you know..."
                )
            )
            list.add(
                Messages(
                    message = "Bruh!"
                )
            )
            list.add(
                Messages(
                    message = "Need the report to be finished soon"
                )
            )
            list.add(
                Messages(
                    message = "See you this weekend"
                )
            )
            list.add(
                Messages(
                    message = "You Shall Not PASS!"
                )
            )
            return list
        }
    }
}
