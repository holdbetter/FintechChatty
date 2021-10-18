package com.holdbetter.chatonrecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.chatonrecycler.components.ForeignMessageLayout
import com.holdbetter.chatonrecycler.components.MessageLayout
import com.holdbetter.chatonrecycler.model.Message

class MessageAdapter(val messages: ArrayList<Message> = ArrayList()) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val root = when (viewType) {
            MessageType.FOREIGN.ordinal -> LayoutInflater.from(parent.context)
                .inflate(R.layout.foreign_message_instance, parent, false)
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.myself_message_instance, parent, false)
        }
        return MessageViewHolder(root)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        if (!message.user.isItMe && holder.messageLayout is ForeignMessageLayout) {
            holder.messageLayout.avatar = message.user.avatarId
            holder.messageLayout.name = message.user.name
        }
        holder.messageLayout.message = message.text
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int =
        if (messages[position].user.isItMe) {
            MessageType.MYSELF.ordinal
        } else {
            MessageType.FOREIGN.ordinal
        }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageLayout = itemView as MessageLayout
    }

    enum class MessageType {
        FOREIGN,
        MYSELF
    }
}