package com.holdbetter.fintechchatproject.domain.repository

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.domain.entity.EmojiListResponse
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toApiEmojiEntityList
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toEmojiEntityList
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toHashtagStream
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toHashtagStreamEntity
import com.holdbetter.fintechchatproject.room.dao.EmojiDao
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import com.holdbetter.fintechchatproject.services.Util
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

class EmojiRepository(private val emojiDao: EmojiDao) : IEmojiRepository {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun getEmojiCached(): Single<Pair<List<EmojiEntity>, List<ApiEmojiEntity>>> {
        return emojiDao.getEmojiVariations()
    }

    override fun getAllEmojiOnline(connectivityManager: ConnectivityManager): Single<EmojiListResponse> {
        return Util.isConnected(connectivityManager)
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getAllEmoji() }
            .doOnSuccess { message -> cacheEmoji(
                    message.toEmojiEntityList(),
                    message.toApiEmojiEntityList()
                )
            }
    }

    override fun cacheEmoji(emojis: List<EmojiEntity>, apiEmojis: List<ApiEmojiEntity>) {
        emojiDao.applyEmoji(emojis, apiEmojis)
            .subscribe()
            .addTo(compositeDisposable)
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }
}