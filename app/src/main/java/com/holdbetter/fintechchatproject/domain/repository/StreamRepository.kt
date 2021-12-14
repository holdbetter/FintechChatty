package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toStreamEntity
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toTopicEntity
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.room.entity.StreamEntity
import com.holdbetter.fintechchatproject.room.entity.StreamWithTopics
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toStream
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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
    override val dataNotifier: BehaviorSubject<List<Stream>> = BehaviorSubject.create()
    override val dataAvailabilityNotifier: BehaviorSubject<Boolean> = BehaviorSubject.create()
    override var streamHolder: List<Stream>? = null

    private val searchRequest: PublishSubject<String> = PublishSubject.create()

    override fun getCachedStreams(): Maybe<List<Stream>> {
        return streamDao.getStreamsWithTopics()
            .subscribeOn(Schedulers.io())
            .map { it.toStream() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { saveLocally(it) }
            .observeOn(Schedulers.io())
    }

    private fun saveLocally(streamList: List<Stream>) {
        streamHolder = streamList
    }

    override fun getStreamsOnline(): Maybe<Any> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getStreams() }
            .map { message -> message.toStreamEntity() }
            .filter { streamList -> streamList.isNotEmpty() }
            .flatMapObservable { streams -> Observable.fromIterable(streams) }
            .flatMapSingle { stream -> getTopicsOnline(stream) }
            .toList()
            .observeOn(Schedulers.computation())
            .flatMap { applySubbedInfoTo(it) }
            .observeOn(Schedulers.io())
            .flatMapCompletable { cacheStreamsAndTopics(it) }
            .andThen(Maybe.just("success"))
    }

    override fun getTopicsOnline(stream: StreamEntity): Single<StreamWithTopics> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
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

    override fun startHandleSearchResults(): Observable<List<Stream>> {
        return searchRequest.subscribeOn(Schedulers.io())
            .debounce(150, TimeUnit.MILLISECONDS)
            .switchMapSingle(::getSearchResponse)
    }

    override fun startObservingStreams(): Observable<List<Stream>> {
        return dataNotifier.subscribeOn(Schedulers.io())
    }

    override fun search(request: String): Single<String> {
        return Single.create {
            searchRequest.onNext(request)
            it.onSuccess(request)
        }
    }

    override fun pushStreams(streamList: List<Stream>) {
        dataNotifier.onNext(streamList)
    }

    private fun getSearchResponse(request: String): Single<List<Stream>> {
        val minimumLengthForSearch = 2
        return if (request.isBlank() || request.length < minimumLengthForSearch) {
            Single.just(streamHolder!!)
        } else {
            streamHolder!!.toObservable()
                .subscribeOn(Schedulers.computation())
                .filter { s -> isMatchingPattern(request, s.name) }
                .toList()
        }
    }

    private fun applySubbedInfoTo(allStreamWithTopics: MutableList<StreamWithTopics>): Single<List<StreamWithTopics>> {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getSubbedStreams() }
            .map { subbedList -> subbedList.toStreamEntity() }
            .map { subbedList -> addSubbedInfoToAllStreams(subbedList, allStreamWithTopics) }
    }

    private fun addSubbedInfoToAllStreams(
        subbedList: List<StreamEntity>,
        allStreamWithTopics: MutableList<StreamWithTopics>
    ): List<StreamWithTopics> {
        for (subbedStream in subbedList) {
            val shouldBeSubbedStream = allStreamWithTopics.find { it.stream.id == subbedStream.id }
            if (shouldBeSubbedStream != null) {
                allStreamWithTopics.remove(shouldBeSubbedStream)
                allStreamWithTopics.add(shouldBeSubbedStream.partialCopy(true))
            }
        }
        return allStreamWithTopics
    }

    private fun isMatchingPattern(searchInput: String, streamNameToCheck: String): Boolean {
        val notUsefulLength = 4
        return if (searchInput.length < notUsefulLength) {
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