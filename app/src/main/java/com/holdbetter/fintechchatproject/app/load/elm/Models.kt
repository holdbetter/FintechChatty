package com.holdbetter.fintechchatproject.app.load.elm

data class EmojiLoadState(
    val error: Throwable? = null
)

sealed class EmojiLoadEvent {
    sealed class Ui : EmojiLoadEvent() {
        object Started : Ui()
        object RetryClicked : Ui()
        object SuccessAnimationsOver : Ui()
    }

    sealed class Internal : EmojiLoadEvent() {
        object Loaded : Internal()
        class LoadingError(val error: Throwable) : Internal()
    }
}

sealed class EmojiLoadEffect {
    object Started : EmojiLoadEffect()
    object CleanUI : EmojiLoadEffect()
    object Loaded : EmojiLoadEffect()
    class ShowError(val error: Throwable) : EmojiLoadEffect()
    object StartNavigation : EmojiLoadEffect()
    object ReloadPage : EmojiLoadEffect()
}

sealed class EmojiLoadCommand {
    object Start : EmojiLoadCommand()
}