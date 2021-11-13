package com.holdbetter.fintechchatproject.main.view

sealed class EmojiLoadedState {
    object Loaded : EmojiLoadedState()
    object Loading : EmojiLoadedState()
    class Error(val exception: Throwable) : EmojiLoadedState()
}