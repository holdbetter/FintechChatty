package com.holdbetter.chatonrecycler.model

import java.util.*
import kotlin.collections.ArrayList

data class Message(
    val user: User,
    val text: String,
    val dateInMillis: Long = Calendar.getInstance().timeInMillis,
    val reactions: ArrayList<Reaction> = arrayListOf(),
)
