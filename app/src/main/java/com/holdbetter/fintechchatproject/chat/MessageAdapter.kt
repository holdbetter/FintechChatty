package com.holdbetter.fintechchatproject.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.chat.view.EmojiBottomModalFragment
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.ui.FlexBoxLayout
import com.holdbetter.fintechchatproject.ui.ForeignMessageLayout
import com.holdbetter.fintechchatproject.ui.IMessageLayout
import com.holdbetter.fintechchatproject.ui.ReactionView

class MessageAdapter(
    val reactionPressedUpdater: ((messageId: Int, emojiCode: String) -> Unit)?,
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val asyncDiffer = AsyncListDiffer(this, MessageDiffUtilCallback())
    private var currentUserId: Long = -1

    val messages: List<Message>
        get() = asyncDiffer.currentList

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
        val message = asyncDiffer.currentList[position]
        if (holder.messageLayout is ForeignMessageLayout) {
            holder.messageLayout.avatar = message.sender.avatarUrl
            holder.messageLayout.name = message.sender.name
        }

        holder.messageLayout.message =
            HtmlCompat.fromHtml(message.htmlSourceMessage, HtmlCompat.FROM_HTML_MODE_LEGACY)
                .removeSuffix("\n\n")

        holder.messageView.setOnLongClickListener {
            val parentFragment = it.findFragment<Fragment>().childFragmentManager
            EmojiBottomModalFragment(message.id).show(parentFragment, EmojiBottomModalFragment.TAG)
            true
        }

        holder.flexbox.removeAllViews()
        for (reaction in message.reactions.groupBy { it.emojiCode }) {
            holder.flexbox.addView(ReactionView(holder.itemView.context).apply {
                emojiUnicode = reaction.key
                count = reaction.value.size
                isSelected = message.sender.id == currentUserId
                setOnClickListener {
//                    reactionPressedUpdater(message.id, reaction.emojiCode)
                }
            })
        }

        holder.flexbox.plusViewOnClickListener = {
            val parentFragment = holder.flexbox.findFragment<Fragment>().childFragmentManager
            EmojiBottomModalFragment(message.id).show(parentFragment, EmojiBottomModalFragment.TAG)
        }
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    override fun getItemViewType(position: Int): Int {
        return if (asyncDiffer.currentList[position].sender.id == currentUserId) {
            MessageType.MYSELF.ordinal
        } else {
            MessageType.FOREIGN.ordinal
        }
    }

    fun submitList(messages: List<Message>, currentUserId: Long) {
        this.currentUserId = currentUserId
        asyncDiffer.submitList(messages)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageLayout = itemView as IMessageLayout
        val flexbox: FlexBoxLayout = itemView.findViewById(R.id.flexbox)
        val messageView: TextView = itemView.findViewById(R.id.message)
    }

    class MessageDiffUtilCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

    enum class MessageType {
        FOREIGN,
        MYSELF
    }
}