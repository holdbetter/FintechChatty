package com.holdbetter.fintechchatproject.navigation.channels.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import com.holdbetter.fintechchatproject.domain.repository.StreamRepository
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.navigation.channels.view.StreamViewState
import com.holdbetter.fintechchatproject.services.ContextExtensions.isConnected
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class StreamViewModel(app: Application) : AndroidViewModel(app) {
    private val streamRepository: IStreamRepository = StreamRepository()
    private var cachedStreams: List<HashtagStream>? = null

    private val _isAllStreamsAvailable: MutableLiveData<Boolean> = MutableLiveData()
    val isAllStreamsAvailable: LiveData<Boolean>
        get() = _isAllStreamsAvailable

    private val _streamViewState: MutableLiveData<StreamViewState> = MutableLiveData()
    val streamViewState: LiveData<StreamViewState>
        get() = _streamViewState

    private val compositeDisposable = CompositeDisposable()
    private val searchRequest = PublishSubject.create<String>()

    init {
        getStreams()
        startHandleSearchRequests()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private fun startHandleSearchRequests() {
        searchRequest.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { _streamViewState.value = StreamViewState.Loading }
            .observeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .switchMapSingle(::getSearchResponse)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { _streamViewState.value = StreamViewState.Result(it) },
                onError = { _streamViewState.value = StreamViewState.Error(it) }
            ).addTo(compositeDisposable)
    }

    fun startSearch(input: String) {
        searchRequest.onNext(input)
    }

    private fun getSearchResponse(request: String): Single<List<HashtagStream>> {
        return if (request.isBlank() || request.length < 2) {
            provideDefaultResult()
        } else {
            return cachedStreams?.let {
                Single.just(it)
                    .subscribeOn(Schedulers.io())
                    .flatMapObservable { streams -> streams.toObservable() }
                    .filter { s -> isMatchingPattern(request, s.name) }
                    .toList()
            } ?: throw Exception("Streams aren't existing")
        }
    }

    private fun provideDefaultResult(): Single<List<HashtagStream>> {
        return Single.just(cachedStreams!!)
    }

    private fun isMatchingPattern(searchInput: String, streamNameToCheck: String): Boolean =
        if (searchInput.length < 4) {
            Regex("^#?$searchInput", RegexOption.IGNORE_CASE)
        } else {
            // Vil(en)*
            val patternStart = searchInput.substring(0, searchInput.length - 2)
            val patternEnd =
                "(${searchInput.substring(searchInput.length - 2 until searchInput.length)})*"
            val pattern = "$patternStart$patternEnd"
            Regex(pattern, RegexOption.IGNORE_CASE)
        }.containsMatchIn(streamNameToCheck)


    fun getStreams() {
        isConnected()
            .doOnSuccess { _streamViewState.value = StreamViewState.Loading }
            .delay(5000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .flatMap { getRepository(it) }
            .flatMap { it.getStreams() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    cacheResponse(it)
                    _streamViewState.value = StreamViewState.Result(it)
                    enableSearch()
                },
                onError = { _streamViewState.value = StreamViewState.Error(it) }
            ).addTo(compositeDisposable)
    }

    private fun cacheResponse(it: List<HashtagStream>?) {
        cachedStreams = it
    }

    private fun enableSearch() {
        _isAllStreamsAvailable.value = true
    }

    fun getTopics(stream: HashtagStream): Single<List<Topic>> {
        return streamRepository.getTopicsForStream(stream.id, stream.name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getRepository(isConnected: Boolean) =
        Single.create<IStreamRepository> { emitter ->
            if (isConnected) {
                emitter.onSuccess(streamRepository)
            } else {
                emitter.onError(NotConnectedException())
            }
        }
}