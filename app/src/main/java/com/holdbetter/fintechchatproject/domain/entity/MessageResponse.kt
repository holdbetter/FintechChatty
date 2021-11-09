package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MessageResponse(
    val result: String,
    val msg: String,
    val messages: List<Message>,

    @field:Json(name = "found_anchor")
    val foundAnchor: Boolean,

    @field:Json(name = "found_oldest")
    val foundOldest: Boolean,

    @field:Json(name = "found_newest")
    val foundNewest: Boolean,

    @field:Json(name = "history_limited")
    val historyLimited: Boolean,

    val anchor: Long,
)