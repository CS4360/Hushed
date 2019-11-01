package com.example.hushed.models

// Suggestion from jon: change this class name to "Message",
// change 'sender' to 'partner'
// also eventually, there may be other fields such as a timestamp
//data class Messages( var message: String, var sender: String )
//
data class Messages( var message: String, var sender: String, var timestamp: String) {
    companion object {
        var comparator: Comparator<Messages> = Comparator { a,b  ->  a.timestamp.compareTo(b.timestamp) }
    }
}
