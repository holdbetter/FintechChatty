package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Stream (
    val name: String,

    @field:Json(name = "stream_id")
    val id: Long,

    val description: String,

    @field:Json(name = "rendered_description")
    val renderedDescription: String,

    @field:Json(name = "invite_only")
    val inviteOnly: Boolean,

    @field:Json(name = "is_web_public")
    val isWebPublic: Boolean,

    @field:Json(name = "stream_post_policy")
    val streamPostPolicy: Long,

    @field:Json(name = "history_public_to_subscribers")
    val historyPublicToSubscribers: Boolean,

    @field:Json(name = "first_message_id")
    val firstMessageID: Long,

    @field:Json(name = "message_retention_days")
    val messageRetentionDays: Any? = null,

    @field:Json(name = "date_created")
    val dateCreated: Long,

    @field:Json(name = "is_announcement_only")
    val isAnnouncementOnly: Boolean
)