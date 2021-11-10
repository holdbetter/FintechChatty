package com.holdbetter.fintechchatproject.chat.view

import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Message

sealed class ChatViewState {
    object Loading : ChatViewState()
    class Result(val messages: List<Message>) : ChatViewState()
    class MessageReceived(val messages: List<Message>) : ChatViewState()
    class MessageSent(val textMessage: String) : ChatViewState()
    class MessageSendError(val error: Throwable) : ChatViewState()
    class Error(val error: Throwable) : ChatViewState()
}