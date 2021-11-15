package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.room.entity.TopicEntity
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface ITopicRepository : IRepository {
    val compositeDisposable: CompositeDisposable

    fun cacheTopics(streamId: Long, topicsToCache: List<TopicEntity>)

    fun getTopicsCached(streamId: Long): Maybe<List<Topic>>
    fun getTopicsForStreamOnline(streamId: Long, streamName: String, connectivityManager: ConnectivityManager): Maybe<List<Topic>>

    fun dispose()
}
