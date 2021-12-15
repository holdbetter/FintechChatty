package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PresenceResponse(
    @field:Json(name = "result")
    val result: String,

    @field:Json(name = "msg")
    val msg: String,

    @field:Json(name = "presences")
    val presences: Map<String, StatusWithType>,

    @field:Json(name = "server_timestamp")
    val serverTimestamp: Double
)

@JsonClass(generateAdapter = true)
class StatusWithType(
    @field:Json(name = "aggregated")
    val aggregated: Status
)

@JsonClass(generateAdapter = true)
class Status(
    @field:Json(name = "client")
    val client: String,

    @field:Json(name = "status")
    val status: String,

    @field:Json(name = "timestamp")
    val timestamp: Long,
)
