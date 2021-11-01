package com.holdbetter.fintechchatproject

import android.app.Application
import com.holdbetter.fintechchatproject.model.repository.ChatRepository
import com.holdbetter.fintechchatproject.model.repository.IChatRepository

class ChatApplication : Application() {
    val repository: IChatRepository = ChatRepository()
}