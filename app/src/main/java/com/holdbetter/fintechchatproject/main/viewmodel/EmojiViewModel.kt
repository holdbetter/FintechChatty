package com.holdbetter.fintechchatproject.main.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.services.Mapper.toEmojiApiList
import com.holdbetter.fintechchatproject.domain.services.Mapper.toReactionList
import com.holdbetter.fintechchatproject.model.Reaction
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class EmojiViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _isEmojiLoaded: MutableLiveData<Boolean> = MutableLiveData()
    val isEmojiLoaded: LiveData<Boolean>
        get() = _isEmojiLoaded

    var originalEmojiList: List<EmojiApi> = emptyList()
    var cleanedEmojiList: List<Reaction> = emptyList()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getEmojiList() {
        _isEmojiLoaded.value = false
        ServiceProvider.getApi()
            .getAllEmoji()
            .subscribeOn(Schedulers.io())
            .doOnSuccess { originalEmojiList = it.toEmojiApiList() }
            .doOnSuccess { cleanedEmojiList = it.toReactionList() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { _isEmojiLoaded.value = true }
            ).addTo(compositeDisposable)
    }
}