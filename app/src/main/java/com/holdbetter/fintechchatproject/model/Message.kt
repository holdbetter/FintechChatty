package com.holdbetter.fintechchatproject.model

data class Message(
    val id: Long,
    val sender: Sender,
    val htmlSourceMessage: String,
    val dateInSeconds: Long,
    val reactions: List<Reaction> = listOf(),
    val topic: Topic? = null
)