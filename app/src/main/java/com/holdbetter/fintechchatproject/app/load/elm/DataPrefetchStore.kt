package com.holdbetter.fintechchatproject.app.load.elm

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

class DataPrefetchStore @Inject constructor(private val actor: DataPrefetchActor) {
    private val store by lazy {
        ElmStore(
            initialState = DataPrefetchState(error = null),
            reducer = DataPrefetchReducer(),
            actor = actor
        )
    }

    fun provide() = store
}