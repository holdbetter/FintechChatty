package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Topic(
    @field:Json(name = "max_id")
    val maxId: Long,
    @field:Json(name = "name")
    val name: String
)