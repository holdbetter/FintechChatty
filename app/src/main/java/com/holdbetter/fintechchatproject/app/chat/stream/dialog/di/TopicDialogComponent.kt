package com.holdbetter.fintechchatproject.app.chat.stream.dialog.di

import com.holdbetter.fintechchatproject.app.chat.stream.dialog.TopicChooserDialog
import com.holdbetter.fintechchatproject.app.chat.stream.dialog.elm.TopicChooserCommand
import com.holdbetter.fintechchatproject.di.AndroidDependencies
import dagger.Component

@Component(dependencies = [AndroidDependencies::class])
interface TopicDialogComponent {
    @Component.Factory
    interface Factory {
        fun create(
            androidDependencies: AndroidDependencies
        ): TopicDialogComponent
    }

    fun inject(topicChooserDialog: TopicChooserDialog)
}