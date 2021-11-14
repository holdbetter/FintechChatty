package com.holdbetter.fintechchatproject.main.viewmodel

import android.net.ConnectivityManager
import androidx.lifecycle.*
import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toEmojiApiList
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toReactionList
import com.holdbetter.fintechchatproject.main.view.EmojiLoadedState
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.services.Util.isConnected
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class EmojiViewModel(val connectivityManager: ConnectivityManager) : ViewModel() {
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
        isConnected(connectivityManager)
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
                emitter.onSuccess(ServiceProvider.api)
            } else {
                emitter.onError(NotConnectedException())
            }
        }
}

class EmojiViewModelFactory(
    private val connectivityManager: ConnectivityManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmojiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmojiViewModel(connectivityManager) as T
        }
        throw IllegalArgumentException("Wrong ViewModel class")
    }
}