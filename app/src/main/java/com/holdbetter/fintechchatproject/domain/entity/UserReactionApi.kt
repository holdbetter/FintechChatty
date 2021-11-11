package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserReactionApi (
    @field:Json(name = "emoji_name")
    val emojiName: String,

    @field:Json(name = "emoji_code")
    val emojiCode: String,

    @field:Json(name = "reaction_type")
    val reactionType: String,

    @field:Json(name = "user_id")
    val userID: Long
)