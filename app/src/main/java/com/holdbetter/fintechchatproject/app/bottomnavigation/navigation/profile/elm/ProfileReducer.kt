package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import javax.inject.Inject

class ProfileReducer @Inject constructor() :
    DslReducer<ProfileEvent, ProfileState, ProfileEffect, ProfileCommand>() {
    override fun Result.reduce(event: ProfileEvent): Any {
        return when (event) {
            ProfileEvent.Ui.Init -> state {
                copy(
                    isLoading = false,
                    user = null,
                    isCacheEmpty = false
                )
            }
            ProfileEvent.Ui.Started -> {
                state { copy(isLoading = true, user = null, isCacheEmpty = false) }
                commands { +ProfileCommand.LoadMyself }
            }
            is ProfileEvent.Internal.OnlineDataError -> {
                effects { +ProfileEffect.ShowError(event.error) }
                commands { +ProfileCommand.GetCachedMyself }
            }
            ProfileEvent.Internal.DataReady -> commands { +ProfileCommand.GetCachedMyself }
            is ProfileEvent.Internal.DataLoaded -> {
                state { copy(isLoading = false, user = event.user) }
            }
            is ProfileEvent.Internal.DbDataError -> {
                state { copy(isLoading = false, user = null) }
                effects { +ProfileEffect.ShowError(event.error) }
            }
            ProfileEvent.Internal.DbEmpty -> {
                state { copy(isLoading = false, isCacheEmpty = true) }
            }
            ProfileEvent.Ui.Retry -> {
                state { copy(isLoading = true, user = null, isCacheEmpty = false) }
                commands { +ProfileCommand.LoadMyself }
            }
        }
    }
}