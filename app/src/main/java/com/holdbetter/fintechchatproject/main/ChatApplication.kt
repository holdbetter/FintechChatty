package com.holdbetter.fintechchatproject.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import com.holdbetter.fintechchatproject.domain.repository.ITopicRepository
import com.holdbetter.fintechchatproject.domain.repository.StreamRepository
import com.holdbetter.fintechchatproject.domain.repository.TopicRepository
import com.holdbetter.fintechchatproject.room.ChatDatabase

class ChatApplication : Application() {
    val database by lazy { ChatDatabase.getDatabase(this) }
    val streamRepository: IStreamRepository by lazy { StreamRepository(database.streamDao()) }
    val topicRepository: ITopicRepository by lazy { TopicRepository(database.topicDao()) }
    val connectivityManager by lazy { getSystemService<ConnectivityManager>()!! }
}