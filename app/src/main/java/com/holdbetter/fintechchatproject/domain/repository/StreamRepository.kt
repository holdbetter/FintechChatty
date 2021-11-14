package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toHashtagStream
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toHashtagStreamEntity
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toTopics
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toHashtagStream
import com.holdbetter.fintechchatproject.services.Util.isConnected
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

class StreamRepository(private val dao: StreamDao) : IStreamRepository {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun getStreamsCached(): Single<List<HashtagStream>> {
        return dao.getStreams()
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
            .doOnSuccess { message -> cache(message.toHashtagStreamEntity()) }
            .map { message -> message.toHashtagStream() }
    }


    override fun cache(streamToCache: List<HashtagStreamEntity>) {
        dao.applyStreams(streamToCache)
            .subscribe()
            .addTo(compositeDisposable)
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }

    override fun getTopicsForStreamOnline(streamId: Long, streamName: String): Single<List<Topic>> {
        return ServiceProvider.api.getStreamTopics(streamId)
            .subscribeOn(Schedulers.io())
            .map { message -> message.toTopics(streamId, streamName) }
    }

    private fun getApi(isConnected: Boolean): Single<TinkoffZulipApi> {
        return Single.create { emitter ->
            if (isConnected) {
                emitter.onSuccess(ServiceProvider.api)
            } else {
                emitter.onError(NotConnectedException())
            }
        }
    }
}