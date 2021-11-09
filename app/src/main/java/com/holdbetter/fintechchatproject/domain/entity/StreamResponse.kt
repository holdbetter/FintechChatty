package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class StreamResponse (
    val result: String,
    val msg: String,
    val streams: List<Stream>
)