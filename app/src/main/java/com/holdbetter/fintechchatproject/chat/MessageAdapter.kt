package com.holdbetter.fintechchatproject.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.chat.view.EmojiBottomModalFragment
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.services.Util
import com.holdbetter.fintechchatproject.ui.FlexBoxLayout
import com.holdbetter.fintechchatproject.ui.ForeignMessageLayout
import com.holdbetter.fintechchatproject.ui.IMessageLayout
import com.holdbetter.fintechchatproject.ui.ReactionView

class MessageAdapter(
    val messages: ArrayList<Message> = ArrayList(),
    val reactionPressedUpdater: (messageId: Int, emojiCode: String) -> Unit,
) :
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

        holder.messageView.setOnLongClickListener {
            val parentFragment = it.findFragment<Fragment>().childFragmentManager
            EmojiBottomModalFragment(message.id).show(parentFragment, EmojiBottomModalFragment.TAG)
            true
        }

        holder.flexbox.removeAllViews()
        for (reaction in message.reactions) {
            holder.flexbox.addView(ReactionView(holder.itemView.context).apply {
                emojiUnicode = reaction.emojiCode
                count = reaction.usersId.size
                isSelected = reaction.usersId.contains(Util.currentUserId)
                setOnClickListener {
                    reactionPressedUpdater(message.id, reaction.emojiCode)
                }
            })
        }

        holder.flexbox.plusViewOnClickListener = {
            val parentFragment = holder.flexbox.findFragment<Fragment>().childFragmentManager
            EmojiBottomModalFragment(message.id).show(parentFragment, EmojiBottomModalFragment.TAG)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].user.isItMe) {
            MessageType.MYSELF.ordinal
        } else {
            MessageType.FOREIGN.ordinal
        }
    }

    fun updateMessage(messageId: Int) {
        val updatedIndex = messages.indexOfFirst { m -> m.id == messageId }
        notifyItemChanged(updatedIndex)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageLayout = itemView as IMessageLayout
        val flexbox: FlexBoxLayout = itemView.findViewById(R.id.flexbox)
        val messageView: TextView = itemView.findViewById(R.id.message)
    }

    enum class MessageType {
        FOREIGN,
        MYSELF
    }
}