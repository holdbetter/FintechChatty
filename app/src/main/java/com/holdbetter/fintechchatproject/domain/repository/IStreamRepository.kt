package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.room.entity.StreamEntity
import com.holdbetter.fintechchatproject.room.entity.StreamWithTopics
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface IStreamRepository : IRepository {
    val dataAvailabilityNotifier: BehaviorSubject<Boolean>
    val dataNotifier: BehaviorSubject<List<Stream>>

    val streamHolder: List<Stream>?

    fun getCachedStreams(): Maybe<List<Stream>>

    fun getStreamsOnline(): Maybe<Any>
    fun getTopicsOnline(stream: StreamEntity): Single<StreamWithTopics>
    fun startObservingStreams(): Observable<List<Stream>>

    fun startHandleSearchResults() : Observable<List<Stream>>
    fun search(request: String): Single<String>

    fun pushStreams(streamList: List<Stream>)

    fun cacheStreamsAndTopics(streamsToCache: List<StreamWithTopics>): Completable
    fun notifyParentsAboutDataAvailability()
}