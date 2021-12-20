package com.holdbetter.fintechchatproject.app.chat.elm.topic

import com.holdbetter.fintechchatproject.app.chat.elm.ChatReducer
import com.holdbetter.fintechchatproject.app.chat.elm.ChatState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import vivid.money.elmslie.core.store.ElmStore

class TopicChatStore @AssistedInject constructor(
    @Assisted("actor") private val actor: TopicChatActor,
    private val reducer: ChatReducer
) {
    private val store by lazy {
        ElmStore(
            initialState = ChatState(),
            reducer,
            actor
        )
    }

    fun provide() = store
}

@AssistedFactory
interface TopicChatStoreFactory {
    fun create(
        @Assisted("actor") actor: TopicChatActor
    ): TopicChatStore
}