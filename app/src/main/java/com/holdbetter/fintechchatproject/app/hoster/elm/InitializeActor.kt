package com.holdbetter.fintechchatproject.app.hoster.elm

import android.util.Log
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor

class InitializeActor(
    private val emojiRepository: IEmojiRepository
) : Actor<CacheCommand, InitializerCacheEvent.Internal> {
    override fun execute(command: CacheCommand): Observable<InitializerCacheEvent.Internal> {
        return when (command) {
            CacheCommand.Initialize -> emojiRepository.getEmojiCached()
                .doOnSuccess { emojiRepository.applyCacheIfNotEmpty(it) }
                .filter { continueIfAnyListIsEmpty(it) }
                .mapEvents(
                    successEventMapper = { _ -> InitializerCacheEvent.Internal.CacheEmpty },
                    completionEvent = InitializerCacheEvent.Internal.CacheNotEmpty,
                    failureEventMapper = { e -> InitializerCacheEvent.Internal.Error(e) }
                )
        }
    }

    private fun continueIfAnyListIsEmpty(emojiListPair: Pair<List<EmojiEntity>, List<ApiEmojiEntity>>): Boolean {
        val (emojisForUI, emojisForApi) = emojiListPair
        return emojisForUI.isEmpty() || emojisForApi.isEmpty()
    }
}