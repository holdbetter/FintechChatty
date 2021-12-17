package com.holdbetter.fintechchatproject.model

sealed class MessageItem {
    abstract val id: Long

    data class Message(
        override val id: Long,
        val sender: Sender,
        val messageContent: String,
        val dateInSeconds: Long,
        val reactions: List<Reaction> = listOf(),
        val topic: Topic? = null
    ): MessageItem() {
        companion object {
            const val NOT_SENT_MESSAGE = -1L
        }
    }

    data class HeaderMessage(
        override val id: Long = Long.MIN_VALUE
    ) : MessageItem()
}

