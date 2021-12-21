package com.holdbetter.fintechchatproject.app.chat.stream.dialog.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import javax.inject.Inject

class TopicChooserReducer @Inject constructor(): DslReducer<TopicChooserEvent, TopicChooserState, TopicChooserEffect, TopicChooserCommand>() {
    override fun Result.reduce(event: TopicChooserEvent): Any {
        return when(event) {
            TopicChooserEvent.Ui.Init -> state { copy(isLoading = false, topics = null) }
            is TopicChooserEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +TopicChooserCommand.GetCachedTopics(event.streamId) }
            }
            is TopicChooserEvent.Internal.TopicsLoaded -> state { copy(isLoading = false, topics = event.topics) }
        }
    }
}