package com.holdbetter.fintechchatproject.main.view

sealed class EmojiState {
    object Loaded : EmojiState()
    object Loading : EmojiState()
    class Error(val exception: Throwable) : EmojiState()
}