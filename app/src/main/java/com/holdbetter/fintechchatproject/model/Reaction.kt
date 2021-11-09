package com.holdbetter.fintechchatproject.model

data class Reaction(
    val userId: Long,
    val emojiCode: String,
    val emojiName: String,
    var reactionType: String
) {
    companion object {
        const val SUPPORTED_REACTION_TYPE = "unicode_emoji"
    }
}