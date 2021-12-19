package com.holdbetter.fintechchatproject.app.chat.elm

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import vivid.money.elmslie.core.store.ElmStore

class ChatStore @AssistedInject constructor(
    @Assisted("actor") private val actor: ChatActor,
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
interface ChatStoreFactory {
    fun create(
        @Assisted("actor") actor: ChatActor
    ): ChatStore
}