package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toHashtagStream
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toHashtagStreamEntity
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.room.dao.TopicDao
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toHashtagStream
import com.holdbetter.fintechchatproject.services.Util.isConnected
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

class StreamRepository(private val streamDao: StreamDao) : IStreamRepository {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun getStreamsCached(): Single<List<HashtagStream>> {
        return streamDao.getStreams()
            .subscribeOn(Schedulers.io())
            .map { dboStream -> dboStream.toHashtagStream() }
    }

    override fun getStreamsOnline(
        connectivityManager: ConnectivityManager,
    ): Single<List<HashtagStream>> {
        return isConnected(connectivityManager)
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getStreams() }
            .doOnSuccess { message -> cacheStreams(message.toHashtagStreamEntity()) }
            .map { message -> message.toHashtagStream() }
    }


    override fun cacheStreams(streamsToCache: List<HashtagStreamEntity>) {
        streamDao.applyStreams(streamsToCache)
            .subscribe()
            .addTo(compositeDisposable)
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }
}