package com.holdbetter.fintechchatproject.domain.entity

import com.holdbetter.fintechchatproject.model.Reaction

data class EmojiApi(
    val emojiName: String,
    val emojiCode: String,
    var reactionType: String = Reaction.SUPPORTED_REACTION_TYPE
)