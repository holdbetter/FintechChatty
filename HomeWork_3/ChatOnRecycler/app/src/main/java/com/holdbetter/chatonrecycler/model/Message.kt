package com.holdbetter.chatonrecycler.model

import java.util.*

data class Message(
    val user: User,
    val text: String,
    val dateInMillis: Long = Calendar.getInstance().timeInMillis,
    val reactions: List<Reaction>? = null,
)
