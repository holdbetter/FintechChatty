package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

class PeopleStore @Inject constructor(
    private val actor: PeopleActor,
    private val reducer: PeopleReducer
) {
    private val store by lazy {
        ElmStore(
            initialState = PeopleState(isLoading = false, users = null),
            reducer,
            actor
        )
    }

    fun provide() = store
}