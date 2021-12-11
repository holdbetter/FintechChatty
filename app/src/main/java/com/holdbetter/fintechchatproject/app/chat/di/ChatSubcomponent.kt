package com.holdbetter.fintechchatproject.app.chat.di

import com.holdbetter.fintechchatproject.app.chat.ChatFragment
import dagger.Subcomponent

@Subcomponent
interface ChatSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatSubcomponent
    }

    fun inject(chatFragment: ChatFragment)
}