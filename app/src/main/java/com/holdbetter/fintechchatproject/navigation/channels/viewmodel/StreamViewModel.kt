package com.holdbetter.fintechchatproject.navigation.channels.viewmodel

import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.navigation.channels.view.StreamViewState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class StreamViewModel(
    private val streamRepository: IStreamRepository,
    private val connectivityManager: ConnectivityManager,
) : ViewModel() {
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
        streamRepository.dispose()
    }

    private fun startHandleSearchRequests() {
        searchRequest.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { _streamViewState.value = StreamViewState.Loading }
            .observeOn(Schedulers.io())
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
        _streamViewState.value = StreamViewState.Loading
        streamRepository.getStreamsCached()
            .delay(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                if (it.isNotEmpty()) {
                    _streamViewState.value = StreamViewState.CacheShowing(it)
                    enableSearch()
                    viewModelCache(it)
                }
            }
            .map { it.isNotEmpty() }
            .observeOn(Schedulers.io())
            .delay(3000, TimeUnit.MILLISECONDS)
            .flatMap { isCacheShowing ->
                streamRepository.getStreamsOnline(connectivityManager)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    _streamViewState.value = StreamViewState.Result(it)
                    enableSearch()
                    viewModelCache(it)
                },
                onError = ::provideCorrectErrorState
            ).addTo(compositeDisposable)
    }

    private fun provideCorrectErrorState(throwable: Throwable) {
        when (streamViewState.value) {
            is StreamViewState.Loading -> {
                _streamViewState.value = StreamViewState.Error(throwable)
            }
        }
    }

    private fun viewModelCache(it: List<HashtagStream>?) {
        cachedStreams = it
    }

    private fun enableSearch() {
        _isAllStreamsAvailable.value = true
    }

    fun getTopics(stream: HashtagStream): Single<List<Topic>> {
        return streamRepository.getTopicsForStreamOnline(stream.id, stream.name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

class StreamViewModelFactory(
    private val streamRepository: IStreamRepository,
    private val connectivityManager: ConnectivityManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StreamViewModel(streamRepository, connectivityManager) as T
        }
        throw IllegalArgumentException("Wrong ViewModel class")
    }
}