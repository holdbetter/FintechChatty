package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toHashtagStreamEntity
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toHashtagStream
import com.holdbetter.fintechchatproject.services.Util.isConnected
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class StreamRepository(
    private val streamDao: StreamDao,
    private val connectivityManager: ConnectivityManager
) : IStreamRepository {
    override val dataAvailabilityNotifier: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private val searchRequest: PublishSubject<String> = PublishSubject.create()

    override fun getStreams(): Maybe<List<Stream>> {
        return streamDao.getStreams()
            .subscribeOn(Schedulers.io())
            .map { dboStream -> dboStream.toHashtagStream() }
    }

    override fun getStreamsOnline(): Completable {
        return isConnected(connectivityManager)
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .delay(3000, TimeUnit.MILLISECONDS)
            .flatMap { api -> api.getStreams() }
            .flatMapCompletable { message -> cacheStreams(message.toHashtagStreamEntity()) }
    }

    override fun cacheStreams(streamsToCache: List<HashtagStreamEntity>): Completable {
        return streamDao.applyStreams(streamsToCache)
    }

    override fun notifyParentsAboutDataAvailability() {
        dataAvailabilityNotifier.onNext(true)
    }

    override fun startHandleSearchRequests(): Observable<List<Stream>> {
        return searchRequest.subscribeOn(Schedulers.io())
            .debounce(100, TimeUnit.MILLISECONDS)
            .switchMapSingle(::getSearchResponse)
    }

    override fun search(input: String) {
        searchRequest.onNext(input)
    }

    private fun getSearchResponse(request: String): Single<List<Stream>> {
        return if (request.isBlank() || request.length < 2) {
            getStreams().toSingle()
        } else {
            return getStreams()
                .subscribeOn(Schedulers.io())
                .flatMapObservable { streams -> streams.toObservable() }
                .filter { s -> isMatchingPattern(request, s.name) }
                .toList()
        }
    }

    private fun isMatchingPattern(searchInput: String, streamNameToCheck: String): Boolean {
        return if (searchInput.length < 4) {
            Regex("^#?$searchInput", RegexOption.IGNORE_CASE)
        } else {
            // Vil(en)*
            val patternStart = searchInput.substring(0, searchInput.length - 2)
            val patternEnd =
                "(${searchInput.substring(searchInput.length - 2 until searchInput.length)})*"
            val pattern = "$patternStart$patternEnd"
            Regex(pattern, RegexOption.IGNORE_CASE)
        }.containsMatchIn(streamNameToCheck)
    }
}