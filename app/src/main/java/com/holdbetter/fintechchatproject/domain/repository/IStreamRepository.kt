package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface IStreamRepository : IRepository {
    val compositeDisposable: CompositeDisposable

    fun cacheStreams(streamsToCache: List<HashtagStreamEntity>)
    fun getStreamsCached(): Single<List<HashtagStream>>
    fun getStreamsOnline(connectivityManager: ConnectivityManager): Single<List<HashtagStream>>

    fun dispose()
}