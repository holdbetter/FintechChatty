package com.holdbetter.fintechchatproject.di

import com.holdbetter.fintechchatproject.app.chat.di.ChatSubcomponent
import dagger.Module

@Module(
    subcomponents = [
        ChatSubcomponent::class
    ]
)
class AppSubcomponents