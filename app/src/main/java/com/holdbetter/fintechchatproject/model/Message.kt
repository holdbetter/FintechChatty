package com.holdbetter.fintechchatproject.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Message(
    val user: StupidUser,
    val text: String,
    val topicId: Int,
    val dateInMillis: Long = Calendar.getInstance().timeInMillis,
    val reactions: ArrayList<Reaction> = arrayListOf(),
    val id: Int = MessageId.createId(),
) : Parcelable {
    object MessageId {
        var count = 0

        fun createId() = ++count
    }
}
