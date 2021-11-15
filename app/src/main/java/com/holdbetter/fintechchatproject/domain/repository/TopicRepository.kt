package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toTopicEntity
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toTopics
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.room.dao.TopicDao
import com.holdbetter.fintechchatproject.room.entity.TopicEntity
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toTopics
import com.holdbetter.fintechchatproject.services.Util.isConnected
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TopicRepository(private val topicDao: TopicDao) : ITopicRepository {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun getTopicsCached(streamId: Long): Maybe<List<Topic>> {
        return topicDao.getTopics(streamId)
            .subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .filter { it.isNotEmpty() }
            .map { it.toTopics() }
    }

    override fun getTopicsForStreamOnline(
        streamId: Long,
        streamName: String,
        connectivityManager: ConnectivityManager,
    ): Maybe<List<Topic>> {
        return isConnected(connectivityManager)
            .subscribeOn(Schedulers.io())
            .delay(4000, TimeUnit.MILLISECONDS)
            .flatMap { getApi(it) }
            .flatMap { it.getStreamTopics(streamId) }
            .subscribeOn(Schedulers.io())
            .doOnSuccess { cacheTopics(streamId, it.toTopicEntity(streamId, streamName)) }
            .map { message -> message.toTopics(streamId, streamName) }
            .toMaybe()
    }

    override fun cacheTopics(streamId: Long, topicsToCache: List<TopicEntity>) {
        topicDao.applyTopics(streamId, topicsToCache)
            .subscribe()
            .addTo(compositeDisposable)
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }
}