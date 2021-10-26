package com.holdbetter.fragmentsandmessaging.model

import android.os.Parcelable
import com.holdbetter.fragmentsandmessaging.services.Util
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class StupidUser(
    val id: Int,
    val name: String,
    val mail: String,
    val avatarResourceId: Int,
    val subscribedTopicListId: ArrayList<Int> = ArrayList(),
    val isOnline: Boolean = false
) : Parcelable {
    @IgnoredOnParcel
    val isItMe: Boolean = Util.currentUserId == id
}
