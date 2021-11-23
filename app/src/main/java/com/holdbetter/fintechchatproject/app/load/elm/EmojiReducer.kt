package com.holdbetter.fintechchatproject.app.load.elm

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

class EmojiReducer :
    DslReducer<EmojiLoadEvent, EmojiLoadState, EmojiLoadEffect, EmojiLoadCommand>() {
    override fun Result.reduce(event: EmojiLoadEvent): Any {
        return when (event) {
            EmojiLoadEvent.Ui.Started -> {
                state { copy(error = null) }
                commands { +EmojiLoadCommand.Start }
                effects {
                    +EmojiLoadEffect.Started
                }
            }
            EmojiLoadEvent.Ui.RetryClicked -> {
                state { copy(error = null) }
                effects {
                    +EmojiLoadEffect.CleanUI
                    +EmojiLoadEffect.ReloadPage
                }
            }
            EmojiLoadEvent.Ui.SuccessAnimationsOver -> {
                effects { +EmojiLoadEffect.StartNavigation }
            }
            EmojiLoadEvent.Internal.Loaded -> effects { +EmojiLoadEffect.Loaded }
            is EmojiLoadEvent.Internal.LoadingError -> {
                state { copy(error = event.error) }
                effects {
                    +EmojiLoadEffect.ShowError(event.error)
                }
            }
        }
    }
}