package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm

import com.holdbetter.fintechchatproject.model.User

data class DetailUserState(
    val isLoading: Boolean,
    val user: User? = null
)

sealed class DetailUserEvent {
    sealed class Ui : DetailUserEvent() {
        object Init : Ui()
        class Started(val userId: Long) : Ui()
    }

    sealed class Internal : DetailUserEvent() {
        class DataLoaded(val user: User) : Internal()
        class DataError(val error: Throwable) : Internal()
    }
}

sealed class DetailUserCommand {
    class LoadUser(val userId: Long) : DetailUserCommand()
}

sealed class DetailUserEffect {
    object ShowError : DetailUserEffect()
}