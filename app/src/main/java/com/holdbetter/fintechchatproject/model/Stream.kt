package com.holdbetter.fintechchatproject.model


data class Stream(
    val id: Long,
    val name: String,
    val subscribed: Boolean = false,
    val topics: List<Topic> = emptyList()
)