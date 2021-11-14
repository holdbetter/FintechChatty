package com.holdbetter.fintechchatproject.room.services

import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity

object DatabaseMapper {
    fun List<HashtagStreamEntity>.toHashtagStream(): List<HashtagStream> {
        return map {
            HashtagStream(
                it.id,
                it.name
            )
        }
    }
}