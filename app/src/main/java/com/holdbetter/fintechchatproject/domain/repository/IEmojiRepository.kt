package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.domain.entity.EmojiListResponse
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface IEmojiRepository : IRepository {
    val compositeDisposable: CompositeDisposable

    fun getEmojiCached(): Single<Pair<List<EmojiEntity>, List<ApiEmojiEntity>>>

    fun getAllEmojiOnline(connectivityManager: ConnectivityManager): Single<EmojiListResponse>

    fun cacheEmoji(emojis: List<EmojiEntity>, apiEmojis: List<ApiEmojiEntity>)

    fun dispose()
}