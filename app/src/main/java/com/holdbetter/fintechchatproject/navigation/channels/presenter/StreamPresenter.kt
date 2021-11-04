package com.holdbetter.fintechchatproject.navigation.channels.presenter

import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.repository.IChatRepository
import com.holdbetter.fintechchatproject.navigation.channels.view.IChannelViewer
import com.holdbetter.fintechchatproject.services.RxExtensions.delayEach
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class StreamPresenter(val chatRepository: IChatRepository, private val channelViewer: IChannelViewer) :
    IStreamPresenter {
    private val compositeDisposable = CompositeDisposable()
    private val searchRequest = PublishSubject.create<String>()

    override fun bind() {
        startHandleSearchRequests()
    }

    private fun startHandleSearchRequests() {
        searchRequest.subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(Schedulers.computation())
            .debounce(250, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { channelViewer.startShimming() }
            .observeOn(Schedulers.io())
            .switchMapSingle(::getResponse)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { channelViewer.stopShimming() }
            .subscribeBy(
                onNext = channelViewer::setStreams
            ).addTo(compositeDisposable)
    }

    override fun unbind() {
        compositeDisposable.clear()
    }

    override fun startSearch(input: String) {
        searchRequest.onNext(input)
    }

    private fun getResponse(request: String): Single<MutableList<HashtagStream>> {
        return if (request.isBlank() || request.length < 2) {
            provideDefaultResult()
        } else {
            chatRepository.hashtagStreams.toObservable()
                .subscribeOn(Schedulers.computation())
                .delayEach(100, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .filter { s -> isMatchingPattern(request, s.name) }
                .toList()
        }
    }

    // ничего себе, это че вайлдкарды
    private fun provideDefaultResult(): Single<MutableList<HashtagStream>> {
        return Single.just(chatRepository.hashtagStreams)
            .subscribeOn(Schedulers.io())
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
}