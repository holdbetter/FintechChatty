package com.holdbetter.fintechchatproject.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.holdbetter.fintechchatproject.room.entity.StreamEntity
import com.holdbetter.fintechchatproject.room.entity.StreamWithTopics
import com.holdbetter.fintechchatproject.room.entity.TopicEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

@Dao
interface StreamDao {
    @Transaction
    @Query("select * from streams")
    fun getStreamsWithTopics(): Maybe<List<StreamWithTopics>>

    @Query("select * from topics where stream_id = :streamId and name = :topicName limit 1")
    fun getTopic(streamId: Long, topicName: String): Single<TopicEntity>

    @Query("select * from topics where stream_id = :streamId and name = :topicName limit 1")
    fun getTopicSynchronously(streamId: Long, topicName: String): TopicEntity

    @Query("select * from topics where stream_id = :streamId order by name")
    fun getTopics(streamId: Long): Single<List<TopicEntity>>

    fun applyStreams(streams: List<StreamWithTopics>): Completable {
        return Completable.create {
            deleteStreamsAndTopics()
            insertStreamsWithTopics(streams)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    @Transaction
    fun insertStreamsWithTopics(streamWithTopics: List<StreamWithTopics>) {
        for (streamWithTopic in streamWithTopics) {
            insertStreamWithTopics(streamWithTopic.stream, streamWithTopic.topics)
        }
    }

    @Insert
    fun insertStreamWithTopics(stream: StreamEntity, topics: List<TopicEntity>)

    @Transaction
    fun deleteStreamsAndTopics() {
        deleteAllTopics()
        deleteAllStreams()
    }

    @Query("delete from streams")
    fun deleteAllStreams()

    @Query("delete from topics")
    fun deleteAllTopics()
}