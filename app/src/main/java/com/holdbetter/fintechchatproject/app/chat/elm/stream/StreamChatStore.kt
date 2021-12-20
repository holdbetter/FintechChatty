package com.holdbetter.fintechchatproject.app.chat.elm.stream

import com.holdbetter.fintechchatproject.app.chat.elm.ChatReducer
import com.holdbetter.fintechchatproject.app.chat.elm.ChatState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import vivid.money.elmslie.core.store.ElmStore

class StreamChatStore @AssistedInject constructor(
    @Assisted("actor") private val actor: StreamChatActor,
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
interface StreamChatStoreFactory {
    fun create(
        @Assisted("actor") actor: StreamChatActor
    ): StreamChatStore
}