package com.holdbetter.fintechchatproject.app.chat.view

import androidx.recyclerview.widget.DiffUtil
import com.holdbetter.fintechchatproject.model.MessageItem

class MessageDiffUtilCallback : DiffUtil.ItemCallback<MessageItem>() {
    override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: MessageItem, newItem: MessageItem): Any? {
        return when {
            oldItem is MessageItem.HeaderLoading || newItem is MessageItem.HeaderLoading -> {
                super.getChangePayload(oldItem, newItem)
            }
            else -> {
                val oldMessage = oldItem as MessageItem.Message
                val newMessage = newItem as MessageItem.Message
                if (oldMessage.reactions != newMessage.reactions) {
                    return "Reactions has changed"
                } else {
                    super.getChangePayload(oldItem, newItem)
                }
            }
        }
    }
}