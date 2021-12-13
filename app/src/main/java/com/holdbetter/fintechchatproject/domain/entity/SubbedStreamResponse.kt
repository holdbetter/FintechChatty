package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SubbedStreamResponse (
    val result: String,
    val msg: String,
    val subscriptions: List<Subscription>
)