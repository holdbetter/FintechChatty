package com.holdbetter.fintechchatproject.model

data class Topic(
    val maxId: Int,
    val name: String,
    val streamId: Long,
    val streamName: String,
    val color: String = "#121212"
)