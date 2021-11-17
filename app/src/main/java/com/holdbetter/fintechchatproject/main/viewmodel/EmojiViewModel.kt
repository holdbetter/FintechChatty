package com.holdbetter.fintechchatproject.main.viewmodel

import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toEmojiApiList
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toReactionList
import com.holdbetter.fintechchatproject.main.view.EmojiState
import com.holdbetter.fintechchatproject.main.view.NavigationState
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toEmojiApiList
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toReactionList
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class EmojiViewModel(
    private val emojiRepository: IEmojiRepository,
    private val connectivityManager: ConnectivityManager,
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _navigationState: MutableLiveData<NavigationState> = MutableLiveData()
    val navigationState: LiveData<NavigationState>
        get() = _navigationState

    private val _isEmojiLoaded: MutableLiveData<EmojiState> = MutableLiveData()
    val isEmojiLoaded: LiveData<EmojiState>
        get() = _isEmojiLoaded

    var originalEmojiList: List<EmojiApi> = emptyList()
    var cleanedEmojiList: List<Reaction> = emptyList()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getEmojiList() {
        _isEmojiLoaded.value = EmojiState.Loading
        emojiRepository.getEmojiCached()
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { applyCacheIfNotEmpty(it) }
            .filter { continueIfAnyListIsEmpty(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { goOnlineState() }
            .observeOn(Schedulers.io())
            .flatMapSingle { emojiRepository.getAllEmojiOnline(connectivityManager) }
            .doOnSuccess { originalEmojiList = it.toEmojiApiList() }
            .doOnSuccess { cleanedEmojiList = it.toReactionList() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { _isEmojiLoaded.value = EmojiState.Loaded },
                onError = { _isEmojiLoaded.value = EmojiState.Error(it) }
            ).addTo(compositeDisposable)
    }

    private fun goOnlineState() {
        _navigationState.value = NavigationState.GoOnline
    }

    private fun continueIfAnyListIsEmpty(emojiListPair: Pair<List<EmojiEntity>, List<ApiEmojiEntity>>): Boolean {
        val (emojisForUI, emojisForApi) = emojiListPair
        return emojisForUI.isEmpty() || emojisForApi.isEmpty()
    }

    private fun applyCacheIfNotEmpty(emojiListPair: Pair<List<EmojiEntity>, List<ApiEmojiEntity>>) {
        val (emojisForUI, emojisForApi) = emojiListPair
        if (emojisForUI.isNotEmpty() && emojisForApi.isNotEmpty()) {
            originalEmojiList = emojisForApi.toEmojiApiList()
            cleanedEmojiList = emojisForUI.toReactionList()
            _navigationState.value = NavigationState.CacheReady
        }
    }
}

class EmojiViewModelFactory(
    private val emojiRepository: IEmojiRepository,
    private val connectivityManager: ConnectivityManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmojiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmojiViewModel(emojiRepository, connectivityManager) as T
        }
        throw IllegalArgumentException("Wrong ViewModel class")
    }
}