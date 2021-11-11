package com.holdbetter.fintechchatproject.domain.retrofit

import com.holdbetter.fintechchatproject.domain.entity.*
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

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

    @FormUrlEncoded
    @POST("messages")
    fun sendMessage(
        @Field("content") content: String,
        @Field("to") streamId: Long,
        @Field("topic") topic: String,
        @Field("type") type: String = "stream",
    ): Single<SentMessageResponse>

    @GET("users")
    fun getUsers(): Single<AllUsersResponse>

    @Headers("isAuthRequired: false")
    @GET("/static/generated/emoji/emoji_codes.json")
    fun getAllEmoji(): Single<EmojiListResponse>
}