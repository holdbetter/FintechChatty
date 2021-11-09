package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserResponse(
    val result: String,
    val msg: String,
    val email: String,

    @field:Json(name = "user_id")
    val userID: Int,

    @field:Json(name = "avatar_version")
    val avatarVersion: Long,

    @field:Json(name = "is_admin")
    val isAdmin: Boolean,

    @field:Json(name = "is_owner")
    val isOwner: Boolean,

    @field:Json(name = "is_guest")
    val isGuest: Boolean,

    @field:Json(name = "is_billing_admin")
    val isBillingAdmin: Boolean,

    val role: Long,

    @field:Json(name = "is_bot")
    val isBot: Boolean,

    @field:Json(name = "full_name")
    val fullName: String,

    val timezone: String,

    @field:Json(name = "is_active")
    val isActive: Boolean,

    @field:Json(name = "date_joined")
    val dateJoined: String,

    @field:Json(name = "avatar_url")
    val avatarURL: String,

    @field:Json(name = "max_message_id")
    val maxMessageID: Long
)