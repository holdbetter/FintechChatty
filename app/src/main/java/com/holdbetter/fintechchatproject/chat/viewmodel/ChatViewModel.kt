package com.holdbetter.fintechchatproject.chat.viewmodel

import androidx.lifecycle.ViewModel
import com.holdbetter.fintechchatproject.domain.repository.ChatRepository
import com.holdbetter.fintechchatproject.domain.repository.IChatRepository
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.User
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ChatViewModel: ViewModel() {
    private val chatRepository: IChatRepository = ChatRepository()

    fun getMessages(getUser: () -> Maybe<User>, narrow: Narrow, currentUserId: Int = -1): Single<List<Message>> {
        return getUser()
            .subscribeOn(Schedulers.io())
            .flatMapSingle { chatRepository.getMessages(narrow) }
            .toSingle()
    }
}