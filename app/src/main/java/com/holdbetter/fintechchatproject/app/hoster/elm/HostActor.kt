package com.holdbetter.fintechchatproject.app.hoster.elm

import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.room.entity.ApiEmojiEntity
import com.holdbetter.fintechchatproject.room.entity.EmojiEntity
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.core.store.Actor
import javax.inject.Inject

class HostActor @Inject constructor(
    private val emojiRepository: IEmojiRepository,
    private val personalRepository: IPersonalRepository
) : Actor<CacheCommand, InitializerCacheEvent.Internal> {
    override fun execute(command: CacheCommand): Observable<InitializerCacheEvent.Internal> {
        return when (command) {
            CacheCommand.Initialize -> emojiRepository.getEmojiCached()
                .filter { continueIfListNotEmpty(it) }
                .flatMap { personalRepository.getCachedMyself() }
                .isEmpty
                .filter { isEmpty -> !isEmpty }
                .mapEvents(
                    successEventMapper = { InitializerCacheEvent.Internal.CacheNotEmpty },
                    completionEvent = InitializerCacheEvent.Internal.CacheEmpty,
                    failureEventMapper = { e -> InitializerCacheEvent.Internal.Error(e) }
                )
        }
    }

    private fun continueIfListNotEmpty(emojiListPair: Pair<List<EmojiEntity>, List<ApiEmojiEntity>>): Boolean {
        val (emojisForUI, emojisForApi) = emojiListPair
        return emojisForUI.isNotEmpty() && emojisForApi.isNotEmpty()
    }
}