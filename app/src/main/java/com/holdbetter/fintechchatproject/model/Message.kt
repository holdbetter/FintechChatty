package com.holdbetter.fintechchatproject.model

sealed class MessageItem {
    abstract val id: Long

    data class Message(
        override val id: Long,
        val sender: Sender,
        val messageContent: String,
        val dateInSeconds: Long,
        val reactions: List<Reaction> = listOf()
    ): MessageItem() {
        companion object {
            const val NOT_SENT_MESSAGE = -1L
            const val RECEIVED_MESSAGE = 0L
        }
    }

    data class HeaderLoading(
        override val id: Long = Long.MIN_VALUE
    ) : MessageItem()
}

