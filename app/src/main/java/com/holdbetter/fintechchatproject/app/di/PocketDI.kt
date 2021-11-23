package com.holdbetter.fintechchatproject.app.di

import android.app.Application
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService
import com.holdbetter.fintechchatproject.app.hoster.elm.InitializeActor
import com.holdbetter.fintechchatproject.app.hoster.elm.InitializerStore
import com.holdbetter.fintechchatproject.app.load.elm.EmojiActor
import com.holdbetter.fintechchatproject.app.load.elm.EmojiLoadingStore
import com.holdbetter.fintechchatproject.domain.repository.*
import com.holdbetter.fintechchatproject.room.ChatDatabase

class PocketDI private constructor(app: Application) {
    val connectivityManager by lazy { getSystemService(app, ConnectivityManager::class.java)!! }

    val database by lazy { ChatDatabase.getDatabase(app) }

    object HostElmProvider {
        private val actor by lazy {
            InitializeActor(INSTANCE.emojiRepository)
        }

        val store by lazy {
            InitializerStore(actor)
        }
    }

    object LoadingElmProvider {
        private val actor by lazy {
            EmojiActor(INSTANCE.emojiRepository)
        }

        val store by lazy {
            EmojiLoadingStore(actor)
        }
    }

    val emojiRepository: IEmojiRepository by lazy { EmojiRepository(database.emojiDao(), connectivityManager) }
    val streamRepository: IStreamRepository by lazy { StreamRepository(database.streamDao()) }
    val topicRepository: ITopicRepository by lazy { TopicRepository(database.topicDao()) }

    companion object {
        lateinit var INSTANCE: PocketDI

        fun init(application: Application) {
            INSTANCE = PocketDI(application)
        }
    }
}