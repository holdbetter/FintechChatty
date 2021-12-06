package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm

import com.holdbetter.fintechchatproject.model.User

data class PeopleState(
    val isLoading: Boolean,
    val users: List<User>? = null
)

sealed class PeopleCommand {
    object LoadPeople : PeopleCommand()
    object GetCachedPeople : PeopleCommand()
}

sealed class PeopleEvent {
    sealed class Ui : PeopleEvent() {
        object Init : Ui()
        object Started : Ui()
        object Retry : Ui()
        class UserClicked(val user: User) : Ui()
    }

    sealed class Internal : PeopleEvent() {
        object DataReady : Internal()
        class DataLoaded(val users: List<User>) : Internal()
        class OnlineDataError(val error: Throwable) : Internal()
        class DbDataError(val error: Throwable) : Internal()
    }
}

sealed class PeopleEffect {
    class ShowError(val error: Throwable) : PeopleEffect()
    class NavigateToUser(val user: User) : PeopleEffect()
    object ShowNoInternetMessage
}