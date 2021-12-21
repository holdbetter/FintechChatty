package com.holdbetter.fintechchatproject.app.chat.stream.dialog.elm

import com.holdbetter.fintechchatproject.room.entity.TopicEntity

data class TopicChooserState(
    val isLoading: Boolean,
    val topics: List<TopicEntity>? = null
)

sealed class TopicChooserEvent {
    sealed class Ui : TopicChooserEvent() {
        object Init : Ui()
        class Started(val streamId: Long) : Ui()
    }

    sealed class Internal : TopicChooserEvent() {
        class TopicsLoaded(val topics: List<TopicEntity>) : Internal()
    }
}

sealed class TopicChooserCommand {
    class GetCachedTopics(val streamId: Long) : TopicChooserCommand()
}

sealed class TopicChooserEffect