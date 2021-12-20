package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.entity.SentMessageResponse
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toMessage
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.room.dao.MessageDao
import com.holdbetter.fintechchatproject.room.entity.MessageWithReactions
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toMessage
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toSender
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TopicChatRepository @AssistedInject constructor(
    private val messageDao: MessageDao,
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi,
    @Assisted("streamId") override val streamId: Long,
    @Assisted("topicName") override val topicName: String,
    @Assisted("originalEmojiList") override val originalEmojiList: List<EmojiApi>,
    @Assisted("meId") override val meId: Long,
) : BaseChatRepository(connectivityManager, api), ITopicChatRepository {
    private val topicNarrow = Narrow.MessageNarrow(streamId, topicName).toJson()

    override val jsonNarrow: String
        get() = topicNarrow

    override fun getCachedMessages(): Maybe<List<MessageItem.Message>> {
        return messageDao.getTopicMessages(streamId, topicName)
            .flatMapObservable { Observable.fromIterable(it) }
            .concatMapEager { appendSender(it) }
            .toList()
            .toMaybe()
    }

    private fun appendSender(messageWithReactions: MessageWithReactions): Observable<MessageItem.Message> {
        return messageDao.getSender(messageWithReactions.message.senderId)
            .observeOn(Schedulers.computation())
            .map { messageWithReactions.toMessage(it.toSender()) }
            .toObservable()
    }

    override fun getFirstPortion(): Single<Pair<Boolean, List<MessageItem.Message>>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getNewestMessages(topicNarrow) }
            .map { it.toMessage() }
            .flatMap { isItLastPortion(it) }
            .doOnSuccess { cacheMessages(streamId, topicName, it.second) }
    }

    override fun cacheMessages(
        streamId: Long,
        topicName: String,
        messagesToCache: List<MessageItem.Message>
    ) {
        messageDao.applyTopicMessages(streamId, topicName, messagesToCache)
    }

    override fun getNextPortion(
        messageAnchorId: Long,
        currentMessages: List<MessageItem.Message>
    ): Single<Pair<Boolean, List<MessageItem.Message>>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getNextPortion(topicNarrow, messageAnchorId) }
            .map { it.toMessage() }
            .map { excludeAnchorMessage(messageAnchorId, it) }
            .map { appendWithChatMessages(it, currentMessages) }
            .flatMap { isItLastPortion(it) }
            .retryWhen { errors -> errors.flatMap { Flowable.timer(5000, TimeUnit.MILLISECONDS) } }
    }

    override fun sendMessage(
        textMessage: String,
    ): Single<SentMessageResponse> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { it.sendMessage(textMessage, streamId, topicName) }
            .retryWhen { errors -> errors.flatMap { Flowable.timer(1000, TimeUnit.MILLISECONDS) } }
    }
}

@AssistedFactory
interface TopicChatRepositoryFactory {
    fun create(
        @Assisted("streamId") streamId: Long,
        @Assisted("originalEmojiList") originalEmojiList: List<EmojiApi>,
        @Assisted("meId") meId: Long,
        @Assisted("topicName") topicName: String,
    ): TopicChatRepository
}