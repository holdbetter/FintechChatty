package com.holdbetter.fintechchatproject.domain.entity

import com.holdbetter.fintechchatproject.model.Reaction

class EmojiApi(
    val emojiCode: String,
    val emojiName: String,
    var reactionType: String = Reaction.SUPPORTED_REACTION_TYPE
)