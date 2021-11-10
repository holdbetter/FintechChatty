package com.holdbetter.fintechchatproject.model

data class Message(
    val id: Long,
    val sender: Sender,
    val messageContent: String,
    val dateInSeconds: Long,
    val reactions: List<Reaction> = listOf(),
    val topic: Topic? = null
) {
    companion object {
        const val NOT_SENT_MESSAGE = -1L
    }
}