package com.holdbetter.fintechchatproject.app.load.elm

import vivid.money.elmslie.core.store.ElmStore

class EmojiLoadingStore(private val actor: EmojiActor) {
    private val store by lazy {
        ElmStore(
            initialState = EmojiLoadState(error = null),
            reducer = EmojiReducer(),
            actor = actor
        )
    }

    fun provide() = store
}