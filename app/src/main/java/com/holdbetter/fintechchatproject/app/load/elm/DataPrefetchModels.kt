package com.holdbetter.fintechchatproject.app.load.elm

data class DataPrefetchState(
    val error: Throwable? = null
)

sealed class DataPrefetchEvent {
    sealed class Ui : DataPrefetchEvent() {
        object Started : Ui()
        object RetryClicked : Ui()
        object SuccessAnimationsOver : Ui()
    }

    sealed class Internal : DataPrefetchEvent() {
        object Loaded : Internal()
        class LoadingError(val error: Throwable) : Internal()
    }
}

sealed class DataPrefetchEffect {
    object Started : DataPrefetchEffect()
    object CleanUI : DataPrefetchEffect()
    object Loaded : DataPrefetchEffect()
    class ShowError(val error: Throwable) : DataPrefetchEffect()
    object StartNavigation : DataPrefetchEffect()
    object ReloadPage : DataPrefetchEffect()
}

sealed class DataPrefetchCommand {
    object Start : DataPrefetchCommand()
}