package com.holdbetter.fintechchatproject.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.holdbetter.fintechchatproject.room.ChatDatabase
import com.holdbetter.fintechchatproject.room.entity.TopicEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

@Dao
interface TopicDao {
    @Query("select * from topics where stream_id = :streamId")
    fun getTopics(streamId: Long): Single<List<TopicEntity>>

    fun applyTopics(streamId: Long, topics: List<TopicEntity>): Completable {
        return Completable.create {
            deleteAllTopicsInStream(streamId)
            insertTopics(*topics.toTypedArray())
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    @Insert
    fun insertTopics(vararg topics: TopicEntity)

    @Query("delete from topics where stream_id = :streamId")
    fun deleteAllTopicsInStream(streamId: Long)
}
