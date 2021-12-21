package com.holdbetter.fintechchatproject.app.chat.stream.dialog.elm

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

class TopicChooserStore @Inject constructor(
    private val actor: TopicChooserActor,
    private val reducer: TopicChooserReducer
) {
    private val store by lazy {
        ElmStore(
            initialState = TopicChooserState(false, null),
            reducer,
            actor
        )
    }

    fun provide() = store
}