package com.holdbetter.fintechchatproject.model


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
                "offline" -> UserStatus.OFFLINE
                "idle" -> UserStatus.IDLE
                "active" -> UserStatus.ACTIVE
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
