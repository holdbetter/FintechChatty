package com.holdbetter.fintechchatproject.app.chat.view

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.model.MessageItem

abstract class BaseMessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val asyncDiffer by lazy {
        AsyncListDiffer(this, MessageDiffUtilCallback())
    }

    val messages: List<MessageItem>
        get() = asyncDiffer.currentList

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    fun submitList(messages: List<MessageItem>, messagesSetCallback: () -> Unit = {}) {
        asyncDiffer.submitList(messages, messagesSetCallback)
    }

    fun sendMessage(message: MessageItem.Message, messageAddedWithLoadingUiCallback: () -> Unit) {
        val currentList = ArrayList(asyncDiffer.currentList)
        currentList.add(message)
        asyncDiffer.submitList(currentList, messageAddedWithLoadingUiCallback)
    }

    fun clear() {
        asyncDiffer.submitList(emptyList())
    }

    enum class MessageType {
        PORTION_LOADING,
        FOREIGN,
        MYSELF,
        LOADING_MYSELF
    }
}