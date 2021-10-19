package com.holdbetter.chatonrecycler.model

import com.holdbetter.chatonrecycler.services.Util

data class User(val userId: Int, val name: String, val avatarId: Int) {
    val isItMe: Boolean = Util.currentUserId == userId

    init {
        Util.users.add(this)
    }
}
