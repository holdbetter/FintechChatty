package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Subscription(
    @field:Json(name = "stream_id") val id: Long,
    val name: String,
    val color: String
)