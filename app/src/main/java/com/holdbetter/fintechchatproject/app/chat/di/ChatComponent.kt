package com.holdbetter.fintechchatproject.app.chat.di

import com.holdbetter.fintechchatproject.app.chat.stream.StreamChatFragment
import com.holdbetter.fintechchatproject.app.chat.topic.TopicChatFragment
import com.holdbetter.fintechchatproject.di.AndroidDependencies
import com.holdbetter.fintechchatproject.di.DomainDependencies
import com.holdbetter.fintechchatproject.di.RepositoryDependencies
import dagger.Component

@Component(
    dependencies = [AndroidDependencies::class, DomainDependencies::class, RepositoryDependencies::class]
)
interface ChatComponent {
    @Component.Factory
    interface Factory {
        fun create(
            androidDependencies: AndroidDependencies,
            domainDependencies: DomainDependencies,
            repositoryDependencies: RepositoryDependencies
        ): ChatComponent
    }

    fun inject(topicChatFragment: TopicChatFragment)
    fun inject(topicChatFragment: StreamChatFragment)
}