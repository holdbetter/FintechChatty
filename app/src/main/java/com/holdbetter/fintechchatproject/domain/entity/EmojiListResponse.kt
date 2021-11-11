package com.holdbetter.fintechchatproject.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class EmojiListResponse (
    @field:Json(name = "codepoint_to_name")
    val codepointToName: Map<String, String>,

    @field:Json(name = "emoji_catalog")
    val emojiCatalog: Map<String, List<String>>,
)