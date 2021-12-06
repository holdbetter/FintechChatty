package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import javax.inject.Inject

class PeopleReducer @Inject constructor(): DslReducer<PeopleEvent, PeopleState, PeopleEffect, PeopleCommand>() {
    override fun Result.reduce(event: PeopleEvent): Any {
        return when (event) {
            PeopleEvent.Ui.Init -> state { copy(isLoading = false, users = null) }
            PeopleEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +PeopleCommand.LoadPeople }
            }
            is PeopleEvent.Internal.OnlineDataError -> {
                commands { +PeopleCommand.GetCachedPeople }
                effects { +PeopleEffect.ShowError(event.error) }
            }
            is PeopleEvent.Internal.DataLoaded -> state {
                copy(
                    isLoading = false,
                    users = event.users
                )
            }
            is PeopleEvent.Ui.UserClicked -> effects { +PeopleEffect.NavigateToUser(event.user) }
            PeopleEvent.Internal.DataReady -> commands { +PeopleCommand.GetCachedPeople }
            is PeopleEvent.Internal.DbDataError -> {
                state { copy(isLoading = false) }
                effects { +PeopleEffect.ShowError(event.error) }
            }
            PeopleEvent.Ui.Retry -> {
                state { copy(isLoading = true) }
                commands { +PeopleCommand.LoadPeople }
            }
        }
    }
}