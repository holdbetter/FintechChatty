package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TopicResponse (
    val result: String,
    val msg: String,
    val topics: List<Topic>
)