package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toHashtagStreamEntity
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toTopicEntity
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.room.entity.StreamEntity
import com.holdbetter.fintechchatproject.room.entity.StreamWithTopics
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toStream
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StreamRepository @Inject constructor(
    private val streamDao: StreamDao,
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi
) : IStreamRepository {
    override val dataAvailabilityNotifier: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private val searchRequest: PublishSubject<String> = PublishSubject.create()

    override fun getStreamsWithTopics(): Maybe<List<Stream>> {
        return streamDao.getStreamsWithTopics()
            .subscribeOn(Schedulers.io())
            .map { it.toStream() }
    }

    override fun getStreamsOnline(): Completable {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .delay(1000, TimeUnit.MILLISECONDS)
            .flatMap { api -> api.getStreams() }
            .map { message -> message.toHashtagStreamEntity() }
            .flatMapObservable { streams -> Observable.fromIterable(streams) }
            .flatMapSingle { stream -> getTopicsOnline(stream) }
            .toList()
            .flatMapCompletable { cacheStreamsAndTopics(it) }
    }

    override fun getTopicsOnline(stream: StreamEntity): Single<StreamWithTopics> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .delay(1000, TimeUnit.MILLISECONDS)
            .flatMap { api -> api.getStreamTopics(stream.id) }
            .map { topicResponse -> topicResponse.toTopicEntity(stream.id, stream.name) }
            .map { topicEntityList -> StreamWithTopics(stream, topicEntityList) }
    }

    override fun cacheStreamsAndTopics(streamsToCache: List<StreamWithTopics>): Completable {
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
            getStreamsWithTopics().toSingle()
        } else {
            return getStreamsWithTopics()
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