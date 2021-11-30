package com.holdbetter.fintechchatproject.app.hoster.elm

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

class HostStore @Inject constructor(private val actor: HostActor) {
    private val store by lazy {
        ElmStore(
            initialState = InitializerCacheState(),
            reducer = HostReducer(),
            actor = actor
        )
    }

    fun provide() = store
}