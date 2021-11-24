package com.holdbetter.fintechchatproject.navigation.channels.elm

import vivid.money.elmslie.core.store.ElmStore

class StreamStore(private val actor: StreamActor) {
    private val store by lazy {
        ElmStore(
            initialState = AllStreamState(isLoading = false),
            reducer = StreamReducer(),
            actor = actor
        )
    }

    fun provide() = store
}