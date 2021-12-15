package com.holdbetter.fintechchatproject.model

import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi


data class User(
    val id: Long,
    val name: String,
    val mail: String,
    val avatarUrl: String,
    val status: UserStatus
) {
    companion object {
        fun statusFromString(status: String): UserStatus {
            return when (status) {
                TinkoffZulipApi.ZulipStatus.OFFLINE -> UserStatus.OFFLINE
                TinkoffZulipApi.ZulipStatus.IDLE -> UserStatus.IDLE
                TinkoffZulipApi.ZulipStatus.ACTIVE -> UserStatus.ACTIVE
                else -> UserStatus.OFFLINE
            }
        }
    }
}

enum class UserStatus {
    OFFLINE,
    IDLE,
    ACTIVE
}
