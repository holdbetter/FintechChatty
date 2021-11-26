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

    fun getStreamsWithTopics(): Maybe<List<Stream>>
    // TODO: 11/25/2021
//    fun getSubbedStreams(): Maybe<List<Stream>>

    fun getStreamsOnline(): Completable
    fun getTopicsOnline(stream: StreamEntity): Single<StreamWithTopics>
    // TODO: 11/25/2021
//    fun getSubbedStreamsOnline(): Completable

    fun startHandleSearchRequests() : Observable<List<Stream>>
    fun search(input: String)

    fun cacheStreamsAndTopics(streamsToCache: List<StreamWithTopics>): Completable
    fun notifyParentsAboutDataAvailability()
}