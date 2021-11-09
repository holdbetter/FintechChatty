package com.holdbetter.fintechchatproject.domain.retrofit

import com.holdbetter.fintechchatproject.domain.entity.MessageResponse
import com.holdbetter.fintechchatproject.domain.entity.StreamResponse
import com.holdbetter.fintechchatproject.domain.entity.TopicResponse
import com.holdbetter.fintechchatproject.domain.entity.UserResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TinkoffZulipApi {
    @GET("streams")
    fun getStreams(): Single<StreamResponse>

    @GET("users/me/{stream_id}/topics")
    fun getStreamTopics(@Path("stream_id") streamId: Long): Single<TopicResponse>

    @GET("users/me")
    fun getMyself(): Single<UserResponse>

    @GET("messages")
    fun getMessages(
        @Query("narrow") jsonNarrow: String,
        @Query("anchor") anchor: String = MessageAnchors.NEWEST.value,
        @Query("num_before") numBefore: Int = 40,
        @Query("num_after") numAfter: Int = 0,
        @Query("apply_markdown") shouldBeHtml: Boolean = true
    ): Single<MessageResponse>
}