package com.holdbetter.fintechchatproject.domain.retrofit

import com.holdbetter.fintechchatproject.domain.entity.*
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface TinkoffZulipApi {
    object ZulipStatus {
        const val OFFLINE = "offline"
        const val ACTIVE = "active"
        const val IDLE = "idle"
    }

    @GET("streams")
    fun getStreams(): Single<StreamResponse>

    @GET("users/me/subscriptions")
    fun getSubbedStreams(): Single<SubbedStreamResponse>

    @GET("users/me/{stream_id}/topics")
    fun getStreamTopics(@Path("stream_id") streamId: Long): Single<TopicResponse>

    @GET("users/me")
    fun getMyself(): Single<UserResponse>

    @GET("messages")
    fun getNewestMessages(
        @Query("narrow") jsonNarrow: String,
        @Query("anchor") anchor: String = MessageAnchors.NEWEST.value,
        @Query("num_before") numBefore: Int = 80,
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

    @FormUrlEncoded
    @POST("messages/{message_id}/reactions")
    fun sendReaction(
        @Path("message_id") messageId: Long,
        @Field("emoji_name") emojiName: String,
        @Field("emoji_code") emojiCode: String,
        @Field("reaction_type") reactionType: String = "unicode_emoji"
    ): Single<ResponseBody>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "messages/{message_id}/reactions", hasBody = true)
    fun removeReaction(
        @Path("message_id") messageId: Long,
        @Field("emoji_name") emojiName: String,
        @Field("emoji_code") emojiCode: String,
        @Field("reaction_type") reactionType: String = "unicode_emoji"
    ): Single<ResponseBody>

    @FormUrlEncoded
    @POST("users/me/presence")
    fun sendStatus(
        @Field("status") status: String = "active"
    ): Single<PresenceResponse>

    @GET("users")
    fun getUsers(): Single<AllUsersResponse>

    @Headers("isAuthRequired: false")
    @GET("/static/generated/emoji/emoji_codes.json")
    fun getAllEmoji(): Single<EmojiListResponse>
}