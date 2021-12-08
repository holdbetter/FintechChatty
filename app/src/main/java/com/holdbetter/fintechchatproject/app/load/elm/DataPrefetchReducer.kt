package com.holdbetter.fintechchatproject.app.load.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class DataPrefetchReducer :
    DslReducer<DataPrefetchEvent, DataPrefetchState, DataPrefetchEffect, DataPrefetchCommand>() {
    override fun Result.reduce(event: DataPrefetchEvent): Any {
        return when (event) {
            DataPrefetchEvent.Ui.Started -> {
                state { copy(error = null) }
                commands { +DataPrefetchCommand.Start }
                effects {
                    +DataPrefetchEffect.Started
                }
            }
            DataPrefetchEvent.Ui.RetryClicked -> {
                state { copy(error = null) }
                effects {
                    +DataPrefetchEffect.CleanUI
                    +DataPrefetchEffect.ReloadPage
                }
            }
            DataPrefetchEvent.Ui.SuccessAnimationsOver -> {
                effects { +DataPrefetchEffect.StartNavigation }
            }
            DataPrefetchEvent.Internal.Loaded -> effects { +DataPrefetchEffect.Loaded }
            is DataPrefetchEvent.Internal.LoadingError -> {
                state { copy(error = event.error) }
                effects {
                    +DataPrefetchEffect.ShowError(event.error)
                }
            }
        }
    }
}