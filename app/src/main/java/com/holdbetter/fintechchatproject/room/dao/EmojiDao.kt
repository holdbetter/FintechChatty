package com.holdbetter.fintechchatproject.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

@Dao
interface EmojiDao {
    @Query("select * from emoji")
    fun getEmoji(): List<EmojiEntity>

    @Query("select * from api_emoji")
    fun getApiEmoji(): List<ApiEmojiEntity>

    fun getEmojiVariations(): Single<Pair<List<EmojiEntity>, List<ApiEmojiEntity>>> {
        return Single.create<Pair<List<EmojiEntity>, List<ApiEmojiEntity>>> {
            val emojiEntityList = getEmoji()
            val apiEmojiEntityList = getApiEmoji()
            it.onSuccess(emojiEntityList to apiEmojiEntityList)
        }.subscribeOn(Schedulers.io())
    }

    fun applyEmoji(emojis: List<EmojiEntity>, apiEmojis: List<ApiEmojiEntity>): Completable {
        return Completable.create {
            deleteAll()
            insertAll(emojis, apiEmojis)
        }.subscribeOn(Schedulers.io())
    }

    @Transaction
    fun insertAll(emojis: List<EmojiEntity>, apiEmojis: List<ApiEmojiEntity>) {
        insertEmoji(*emojis.toTypedArray())
        insertApiEmoji(*apiEmojis.toTypedArray())
    }

    @Insert
    fun insertEmoji(vararg emoji: EmojiEntity)

    @Insert
    fun insertApiEmoji(vararg apiEmojis: ApiEmojiEntity)

    @Transaction
    fun deleteAll() {
        deleteApiEmoji()
        deleteEmoji()
    }

    @Query("delete from api_emoji")
    fun deleteApiEmoji()

    @Query("delete from emoji")
    fun deleteEmoji()
}