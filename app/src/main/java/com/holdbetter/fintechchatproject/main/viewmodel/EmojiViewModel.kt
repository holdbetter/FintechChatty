package com.holdbetter.fintechchatproject.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.Mapper.toEmojiApiList
import com.holdbetter.fintechchatproject.domain.services.Mapper.toReactionList
import com.holdbetter.fintechchatproject.main.view.EmojiLoadedState
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.services.ContextExtensions.isConnected
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class EmojiViewModel(app: Application) : AndroidViewModel(app) {
    private val compositeDisposable = CompositeDisposable()

    private val _isEmojiLoaded: MutableLiveData<EmojiLoadedState> = MutableLiveData()
    val isEmojiLoaded: LiveData<EmojiLoadedState>
        get() = _isEmojiLoaded

    var originalEmojiList: List<EmojiApi> = emptyList()
    var cleanedEmojiList: List<Reaction> = emptyList()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getEmojiList() {
        isConnected()
            .doOnSuccess { _isEmojiLoaded.value = EmojiLoadedState.Loading }
            .observeOn(Schedulers.io())
            .flatMap { isConnected -> getApi(isConnected) }
            .flatMap { api -> api.getAllEmoji() }
            .doOnSuccess { originalEmojiList = it.toEmojiApiList() }
            .doOnSuccess { cleanedEmojiList = it.toReactionList() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { _isEmojiLoaded.value = EmojiLoadedState.Loaded },
                onError = { _isEmojiLoaded.value = EmojiLoadedState.Error(it) }
            ).addTo(compositeDisposable)
    }

    private fun getApi(isConnected: Boolean) =
        Single.create<TinkoffZulipApi> { emitter ->
            if (isConnected) {
                emitter.onSuccess(ServiceProvider.getApi())
            } else {
                emitter.onError(NotConnectedException())
            }
        }
}