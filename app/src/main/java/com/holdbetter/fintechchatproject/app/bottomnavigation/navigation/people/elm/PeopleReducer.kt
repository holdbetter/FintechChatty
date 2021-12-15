package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import javax.inject.Inject

class PeopleReducer @Inject constructor() :
    DslReducer<PeopleEvent, PeopleState, PeopleEffect, PeopleCommand>() {
    override fun Result.reduce(event: PeopleEvent): Any {
        return when (event) {
            PeopleEvent.Ui.Init -> {
                state { copy(isLoading = false, users = null) }
                commands { +PeopleCommand.ObserveSearching }
            }
            PeopleEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +PeopleCommand.LoadPeople }
            }
            is PeopleEvent.Internal.OnlineDataError -> {
                commands { +PeopleCommand.GetCachedPeopleWithoutPresence }
                effects { +PeopleEffect.ShowError(event.error) }
            }
            is PeopleEvent.Internal.DataLoaded -> {
                state { copy(isLoading = false, users = event.users) }
                effects { +PeopleEffect.EnableSearch }
            }

            is PeopleEvent.Ui.UserClicked -> effects { +PeopleEffect.NavigateToUser(event.user) }
            PeopleEvent.Internal.DataReady -> commands { +PeopleCommand.GetCachedPeople }
            is PeopleEvent.Internal.DbDataError -> {
                state { copy(isLoading = false) }
                effects { +PeopleEffect.ShowError(event.error) }
            }
            PeopleEvent.Ui.Retry -> {
                state { copy(isLoading = true, users = null) }
                commands { +PeopleCommand.LoadPeople }
            }
            is PeopleEvent.Internal.Searched -> effects { +PeopleEffect.ShowSearchedData(event.users) }
            is PeopleEvent.Ui.Searching -> {
                commands { +PeopleCommand.RunSearch(event.request) }
            }
        }
    }
}