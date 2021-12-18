package com.holdbetter.fintechchatproject.model

data class Reaction(
    val userId: Long,
    val emojiName: String,
    val emojiCode: String,
    val reactionType: String = SUPPORTED_REACTION_TYPE
) {
    companion object {
        const val SUPPORTED_REACTION_TYPE = "unicode_emoji"
    }
}