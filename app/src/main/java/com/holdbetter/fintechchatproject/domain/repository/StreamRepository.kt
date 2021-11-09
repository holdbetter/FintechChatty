package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.services.Mapper.toHashtagStream
import com.holdbetter.fintechchatproject.domain.services.Mapper.toTopics
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class StreamRepository : IStreamRepository {
    private val api = ServiceProvider.getApi()

    override fun getStreams(): Single<List<HashtagStream>> {
        return api.getStreams()
            .subscribeOn(Schedulers.io())
            .map { message -> message.toHashtagStream() }
    }

    override fun getTopicsForStream(streamId: Long): Single<List<Topic>> {
        return api.getStreamTopics(streamId)
            .subscribeOn(Schedulers.io())
            .map { message -> message.toTopics(streamId) }
    }
}