package com.holdbetter.fragmentsandmessaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fragmentsandmessaging.components.FlexBoxLayout
import com.holdbetter.fragmentsandmessaging.components.ForeignMessageLayout
import com.holdbetter.fragmentsandmessaging.components.MessageLayout
import com.holdbetter.fragmentsandmessaging.components.ReactionView
import com.holdbetter.fragmentsandmessaging.model.Message
import com.holdbetter.fragmentsandmessaging.services.Util
import java.lang.ref.WeakReference

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
            holder.messageLayout.avatar = message.user.avatarResourceId
            holder.messageLayout.name = message.user.name
        }
        holder.messageLayout.message = message.text

        for (reaction in message.reactions) {
            holder.flexbox.addView(ReactionView(reaction, holder.itemView.context).apply {
                emojiUnicode = reaction.emojiCode
                count = reaction.users_id.size
                isSelected = reaction.users_id.any { id -> id == Util.currentUserId }
            })
        }
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

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageLayout = itemView as MessageLayout
        val flexbox: FlexBoxLayout = itemView.findViewById(R.id.flexbox)
        private val messageView = itemView.findViewById<TextView>(R.id.message)

        init {
            messageView.setOnLongClickListener {
                val emojiBottomModalFragment = EmojiBottomModalFragment(WeakReference(itemView))
                emojiBottomModalFragment.show((it.context as FragmentActivity).supportFragmentManager,
                    EmojiBottomModalFragment.TAG)
                true
            }
        }
    }

    enum class MessageType {
        FOREIGN,
        MYSELF
    }
}