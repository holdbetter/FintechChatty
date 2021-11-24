package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.navigation.channels.elm.StreamEvent
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

interface IStreamRepository : IRepository {
    val dataAvailabilityNotifier: BehaviorSubject<Boolean>

    fun getStreams(): Maybe<List<Stream>>
    fun getStreamsOnline(): Completable

    fun startHandleSearchRequests() : Observable<List<Stream>>
    fun search(input: String)

    fun cacheStreams(streamsToCache: List<HashtagStreamEntity>): Completable
    fun notifyParentsAboutDataAvailability()
}