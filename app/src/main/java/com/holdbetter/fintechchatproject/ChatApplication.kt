package com.holdbetter.fintechchatproject

import android.app.Application
import com.holdbetter.fintechchatproject.model.ChatRepository
import com.holdbetter.fintechchatproject.model.IChatRepository

class ChatApplication : Application() {
    val repository: IChatRepository = ChatRepository()
}