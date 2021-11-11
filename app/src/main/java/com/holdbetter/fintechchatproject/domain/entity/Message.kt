package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Message(
    val id: Long,

    @field:Json(name = "sender_id")
    val senderID: Long,

    val content: String,

    @field:Json(name = "recipient_id")
    val recipientID: Long,

    val timestamp: Long,
    val client: String,
    val subject: String,

    @field:Json(name = "topic_links")
    val topicLinks: List<Any?>,

    @field:Json(name = "is_me_message")
    val isMeMessage: Boolean,

    @field:Json(name = "reactions")
    val userReactions: List<UserReactionApi>,
    val flags: List<String>,

    @field:Json(name = "sender_full_name")
    val senderFullName: String,

    @field:Json(name = "sender_email")
    val senderEmail: String,

    val type: String,

    @field:Json(name = "stream_id")
    val streamID: Long,

    @field:Json(name = "avatar_url")
    val avatarURL: String,

    @field:Json(name = "content_type")
    val contentType: String
)