package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.SentMessageResponse
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.services.Mapper.toMessage
import com.holdbetter.fintechchatproject.model.Message
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ChatRepository: IChatRepository {
    override fun getMessages(narrow: Narrow): Single<List<Message>> {
        val jsonNarrow = narrow.toJson()
        return ServiceProvider.getApi()
            .getMessages(jsonNarrow)
            .subscribeOn(Schedulers.io())
            .map { it.toMessage() }
    }

    override fun sendMessage(
        streamId: Long,
        topicName: String,
        textMessage: String,
    ): Single<SentMessageResponse> {
        return ServiceProvider.getApi()
            .sendMessage(textMessage, streamId, topicName)
            .subscribeOn(Schedulers.io())
    }
}