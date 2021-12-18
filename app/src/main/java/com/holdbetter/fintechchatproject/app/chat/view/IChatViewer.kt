package com.holdbetter.fintechchatproject.app.chat.view

import android.view.View
import android.widget.EditText
import com.holdbetter.fintechchatproject.model.MessageItem

interface IChatViewer {
    fun loading(turnOn: Boolean)
    fun setMessages(messages: List<MessageItem>, isLastPortion: Boolean)
    fun onChatEdgeReaching()
    fun onSendClicked(view: View)
    fun onMessageSent(inputMessage: EditText)
    fun onMessageLongClicked(messageId: Long): Boolean
    fun onReactionPressed(isReactionSelectedNow: Boolean, messageId: Long, emojiName: String)
}
