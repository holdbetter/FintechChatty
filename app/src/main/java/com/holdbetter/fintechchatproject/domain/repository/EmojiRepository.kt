package com.holdbetter.fintechchatproject.domain.repository

import com.holdbetter.fintechchatproject.domain.entity.EmojiApi
import com.holdbetter.fintechchatproject.domain.entity.EmojiListResponse
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toApiEmojiEntityList
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toEmojiApiList
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toEmojiEntityList
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toReactionList
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.room.dao.EmojiDao
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toEmojiApiList
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toReactionList
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class EmojiRepository @Inject constructor(
    private val emojiDao: EmojiDao,
    private val connectivityManager: MyConnectivityManager,
    override val api: TinkoffZulipApi
) : IEmojiRepository {
    override var originalEmojiList: List<EmojiApi> = emptyList()
    override var cleanedEmojiList: List<Reaction> = emptyList()

    override fun getEmojiCached(): Single<Pair<List<EmojiEntity>, List<ApiEmojiEntity>>> {
        return emojiDao.getEmojiVariations()
    }

    override fun getAllEmojiOnline(): Completable {
        return connectivityManager.isConnected
            .subscribeOn(Schedulers.io())
            .flatMap { getApi(it) }
            .flatMap { api -> api.getAllEmoji() }
            .doOnSuccess { saveLocal(it) }
            .flatMapCompletable { message ->
                cacheEmoji(
                    message.toEmojiEntityList(),
                    message.toApiEmojiEntityList()
                )
            }
    }

    override fun saveLocal(emojiListResponse: EmojiListResponse) {
        originalEmojiList = emojiListResponse.toEmojiApiList()
        cleanedEmojiList = emojiListResponse.toReactionList()
    }

    override fun cacheEmoji(
        emojis: List<EmojiEntity>,
        apiEmojis: List<ApiEmojiEntity>
    ): Completable {
        return emojiDao.applyEmoji(emojis, apiEmojis)
    }

    override fun applyCacheIfNotEmpty(emojiListPair: Pair<List<EmojiEntity>, List<ApiEmojiEntity>>) {
        val (emojisForUI, emojisForApi) = emojiListPair
        if (emojisForUI.isNotEmpty() && emojisForApi.isNotEmpty()) {
            originalEmojiList = emojisForApi.toEmojiApiList()
            cleanedEmojiList = emojisForUI.toReactionList()
        }
    }
}