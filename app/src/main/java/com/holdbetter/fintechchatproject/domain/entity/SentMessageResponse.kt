package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SentMessageResponse(
    val result: String,
    val msg: String,
    @field:Json(name = "id")
    val receivedMessageId: Long
)