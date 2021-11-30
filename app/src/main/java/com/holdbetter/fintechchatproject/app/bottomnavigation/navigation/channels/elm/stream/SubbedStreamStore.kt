package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

class SubbedStreamStore @Inject constructor(private val actor: StreamActor) {
    private val store by lazy {
        ElmStore(
            initialState = StreamState(isLoading = false),
            reducer = SubbedStreamReducer(),
            actor = actor
        )
    }

    fun provide() = store
}