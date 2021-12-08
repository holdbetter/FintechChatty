package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.elm

import com.holdbetter.fintechchatproject.model.User

data class ProfileState(
    val isLoading: Boolean,
    val user: User? = null,
    val isCacheEmpty: Boolean = false
)

sealed class ProfileEvent {
    sealed class Ui : ProfileEvent() {
        object Init : Ui()
        object Started : Ui()
        object Retry : Ui()
    }

    sealed class Internal : ProfileEvent() {
        object DataReady : Internal()
        class DataLoaded(val user: User) : Internal()
        class OnlineDataError(val error: Throwable) : Internal()
        class DbDataError(val error: Throwable) : Internal()
        object DbEmpty : Internal()
    }
}

sealed class ProfileCommand {
    object LoadMyself : ProfileCommand()
    object GetCachedMyself : ProfileCommand()
}

sealed class ProfileEffect {
    class ShowError(val error: Throwable) : ProfileEffect()
}