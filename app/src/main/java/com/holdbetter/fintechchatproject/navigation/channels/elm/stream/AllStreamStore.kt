package com.holdbetter.fintechchatproject.navigation.channels.elm.stream

import vivid.money.elmslie.core.store.ElmStore

class AllStreamStore(private val actor: StreamActor) {
    private val store by lazy {
        ElmStore(
            initialState = StreamState(isLoading = false),
            reducer = AllStreamReducer(),
            actor = actor
        )
    }

    fun provide() = store
}