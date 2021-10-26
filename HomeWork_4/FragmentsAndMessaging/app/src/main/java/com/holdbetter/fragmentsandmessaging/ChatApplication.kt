package com.holdbetter.fragmentsandmessaging

import android.app.Application
import com.holdbetter.fragmentsandmessaging.model.ChatRepository
import com.holdbetter.fragmentsandmessaging.model.IChatRepository

class ChatApplication : Application() {
    val repository: IChatRepository = ChatRepository()
}