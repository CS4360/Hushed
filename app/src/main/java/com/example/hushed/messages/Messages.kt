package com.example.hushed.messages


data class Messages( var message: String, var sender: String, var timestamp: String) {
    companion object {
        var comparator: Comparator<Messages> = Comparator { a, b  ->  a.timestamp.compareTo(b.timestamp) }
    }
}
