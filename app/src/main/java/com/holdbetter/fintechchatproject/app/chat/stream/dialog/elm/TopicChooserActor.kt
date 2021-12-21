package com.holdbetter.fintechchatproject.app.chat.stream.dialog.elm

import com.holdbetter.fintechchatproject.room.dao.StreamDao
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class TopicChooserActor @Inject constructor(
    val streamDao: StreamDao
) : Actor<TopicChooserCommand, TopicChooserEvent> {
    override fun execute(command: TopicChooserCommand): Observable<TopicChooserEvent> {
        return when (command) {
            is TopicChooserCommand.GetCachedTopics -> streamDao.getTopics(command.streamId)
                .mapSuccessEvent(
                    successEventMapper = { cachedTopics ->
                        TopicChooserEvent.Internal.TopicsLoaded(
                            cachedTopics
                        )
                    }
                )
        }
    }
}