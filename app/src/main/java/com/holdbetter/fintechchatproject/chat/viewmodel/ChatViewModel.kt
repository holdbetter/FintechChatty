package com.holdbetter.fintechchatproject.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.holdbetter.fintechchatproject.chat.view.ChatViewState
import com.holdbetter.fintechchatproject.domain.repository.ChatRepository
import com.holdbetter.fintechchatproject.domain.repository.IChatRepository
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ChatViewModel : ViewModel() {
    private val chatRepository: IChatRepository = ChatRepository()
    private val compositeDisposable = CompositeDisposable()

    private val _chatViewState: MutableLiveData<ChatViewState> = MutableLiveData()
    val chatViewState: LiveData<ChatViewState>
        get() = _chatViewState

    fun getMessages(getUser: () -> Maybe<User>, narrow: Narrow) {
        _chatViewState.value = ChatViewState.Loading
        getUser().subscribeOn(Schedulers.io())
            .flatMapSingle { chatRepository.getMessages(narrow) }
            .toSingle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { _chatViewState.value = ChatViewState.Result(it) },
                onError = { _chatViewState.value = ChatViewState.Error(it) }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun getMessages(narrow: Narrow): Single<List<Message>> {
        return chatRepository.getMessages(narrow)
    }

    fun sendMessage(
        streamId: Long,
        topicName: String,
        textMessage: String,
    ) {
        _chatViewState.value = ChatViewState.MessageSent(textMessage)
        chatRepository.sendMessage(streamId, topicName, textMessage)
            .subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .flatMap { getMessages(Narrow.MessageNarrow(streamId, topicName)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { _chatViewState.value = ChatViewState.MessageReceived(it) },
                onError = { _chatViewState.value = ChatViewState.MessageSendError(it) }
            ).addTo(compositeDisposable)
    }
}