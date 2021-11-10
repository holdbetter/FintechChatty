package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.Stream
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic
import io.reactivex.rxjava3.core.Single

interface IStreamRepository {
    fun getStreams(): Single<List<HashtagStream>>
    fun getTopicsForStream(streamId: Long, streamName: String): Single<List<Topic>>
}