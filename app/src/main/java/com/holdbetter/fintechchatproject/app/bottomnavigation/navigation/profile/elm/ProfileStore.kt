package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.elm

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

class ProfileStore @Inject constructor(
    private val actor: ProfileActor,
    private val profileReducer: ProfileReducer
) {
    private val store by lazy {
        ElmStore(
            initialState = ProfileState(isLoading = false, user = null, isCacheEmpty = false),
            reducer = profileReducer,
            actor = actor
        )
    }

    fun provide() = store
}