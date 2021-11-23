package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.entity.EmojiListResponse
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface IEmojiRepository : IRepository {
    var originalEmojiList: List<EmojiApi>
    var cleanedEmojiList: List<Reaction>

    fun getEmojiCached(): Single<Pair<List<EmojiEntity>, List<ApiEmojiEntity>>>

    fun getAllEmojiOnline(): Completable

    fun cacheEmoji(emojis: List<EmojiEntity>, apiEmojis: List<ApiEmojiEntity>): Completable

    fun applyCacheIfNotEmpty(emojiListPair: Pair<List<EmojiEntity>, List<ApiEmojiEntity>>)

    fun saveLocal(emojiListResponse: EmojiListResponse)
}