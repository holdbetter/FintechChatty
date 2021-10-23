package com.holdbetter.fragmentsandmessaging.model

import com.holdbetter.fragmentsandmessaging.services.Util

data class User(val userId: Int, val name: String, val avatarId: Int) {
    val isItMe: Boolean = Util.currentUserId == userId

    init {
        Util.users.add(this)
    }
}
