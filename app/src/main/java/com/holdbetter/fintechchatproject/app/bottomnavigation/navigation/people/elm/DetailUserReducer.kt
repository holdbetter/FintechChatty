package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
import javax.inject.Inject

class DetailUserReducer @Inject constructor() :
    DslReducer<DetailUserEvent, DetailUserState, DetailUserEffect, DetailUserCommand>() {
    override fun Result.reduce(event: DetailUserEvent): Any {
        return when (event) {
            is DetailUserEvent.Internal.DataLoaded -> {
                state { copy(isLoading = false, user = event.user)}
            }
            DetailUserEvent.Ui.Init -> state { copy(isLoading = false, user = null) }
            is DetailUserEvent.Ui.Started -> {
                state { copy(isLoading = true) }
                commands { +DetailUserCommand.LoadUser(event.userId) }
            }
            is DetailUserEvent.Internal.DataError -> {
                state { copy(isLoading = false)  }
                effects { +DetailUserEffect.ShowError }
            }
        }
    }
}