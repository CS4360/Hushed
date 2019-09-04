package com.example.hushed

import com.example.hushed.models.Messages

class DataSource {

    companion object {

        fun createDataSet(): ArrayList<Messages> {
            val list = ArrayList<Messages>()
            list.add(
                Messages(
                    sender = "Friend Unit 1",
                    message = "Hey there!"
                )
            )
            list.add(
                Messages(
                    sender = "Parental Unit 1",
                    message = "Please call me back"
                )
            )
            list.add(
                Messages(
                    sender = "Friend Unit 2",
                    message = "Just wanted to let you know..."
                )
            )
            list.add(
                Messages(
                    sender = "Sibling Unit 1",
                    message = "Please don't tell Parental Unit 1 about this"
                )
            )
            list.add(
                Messages(
                    sender = "Friend Unit 3",
                    message = "Bruh!"
                )
            )
            list.add(
                Messages(
                    sender = "Group Member 1",
                    message = "Need the report to be finished soon"
                )
            )
            list.add(
                Messages(
                    sender = "Parental Unit 2",
                    message = "See you this weekend"
                )
            )
            list.add(
                Messages(
                    sender = "Gandalf the Grey",
                    message = "You Shall Not PASS!"
                )
            )
            return list
        }
    }
}
