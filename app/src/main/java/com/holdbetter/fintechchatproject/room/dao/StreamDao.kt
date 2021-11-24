package com.holdbetter.fintechchatproject.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.holdbetter.fintechchatproject.room.ChatDatabase
import com.holdbetter.fintechchatproject.room.entity.HashtagStreamEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

@Dao
interface StreamDao {
    @Query("select * from streams")
    fun getStreams(): Maybe<List<HashtagStreamEntity>>

    fun applyStreams(streams: List<HashtagStreamEntity>): Completable {
        return Completable.create {
            deleteAllStreams()
            insertStreams(*streams.toTypedArray())
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    @Insert
    fun insertStreams(vararg streams: HashtagStreamEntity)

    @Query("delete from streams")
    fun deleteAllStreams()
}