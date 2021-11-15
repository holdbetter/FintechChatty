package com.holdbetter.fintechchatproject.model

data class Topic(
    val maxId: Long,
    val name: String,
    val streamId: Long,
    val streamName: String,
    val color: String = TOPIC_DEFAULT_HEX
) {
    companion object {
        const val TOPIC_DEFAULT_HEX = "#899694"
    }
}