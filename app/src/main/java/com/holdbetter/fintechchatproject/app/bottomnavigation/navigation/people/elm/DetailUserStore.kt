package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

class DetailUserStore @Inject constructor(
    private val detailUserActor: DetailUserActor,
    private val detailUserReducer: DetailUserReducer
) {
    val store by lazy {
        ElmStore(
            initialState = DetailUserState(isLoading = false, user = null),
            reducer = detailUserReducer,
            actor = detailUserActor
        )
    }

    fun provide() = store
}