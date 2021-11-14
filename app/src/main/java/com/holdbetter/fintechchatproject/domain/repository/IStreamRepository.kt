package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface IStreamRepository {
    val compositeDisposable: CompositeDisposable

    fun cache(streamToCache: List<HashtagStreamEntity>)
    fun getStreamsCached(): Single<List<HashtagStream>>
    fun getStreamsOnline(connectivityManager: ConnectivityManager): Single<List<HashtagStream>>
    fun getTopicsForStreamOnline(streamId: Long, streamName: String): Single<List<Topic>>
    fun dispose()
}