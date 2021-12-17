package com.holdbetter.fintechchatproject.app.chat.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.ui.FlexBoxLayout
import com.holdbetter.fintechchatproject.ui.ForeignMessageLayout
import com.holdbetter.fintechchatproject.ui.IMessageLayout
import com.holdbetter.fintechchatproject.ui.ReactionView

class MessageAdapter(
    private val currentUserId: Long,
    private val reactionListener: (
        isReactionSelectedNow: Boolean,
        messageId: Long,
        emojiName: String,
    ) -> Unit,
    private val emojiDialogShower: (messageId: Long) -> Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val asyncDiffer = AsyncListDiffer(this, MessageDiffUtilCallback())

    val messages: List<Message>
        get() = asyncDiffer.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val root: View
        val i = LayoutInflater.from(parent.context)
        return when (viewType) {
            MessageType.LOADING_MYSELF.ordinal -> {
                root = LayoutInflater.from(parent.context)
                    .inflate(R.layout.myself_message_loading_instance, parent, false)
                LoadingViewHolder(root)
            }
            MessageType.MYSELF.ordinal -> {
                root = i.inflate(R.layout.myself_message_instance, parent, false)
                MessageViewHolder(root, reactionListener, emojiDialogShower)
            }
            else -> {
                root = i.inflate(R.layout.foreign_message_instance, parent, false)
                MessageViewHolder(root, reactionListener, emojiDialogShower)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = asyncDiffer.currentList[position]
        when (holder) {
            is MessageViewHolder -> holder.bind(message, currentUserId)
            is LoadingViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    override fun getItemViewType(position: Int): Int {
        val message = asyncDiffer.currentList[position]
        return when (message.sender.id) {
            currentUserId -> {
                when (message.id) {
                    Message.NOT_SENT_MESSAGE -> {
                        MessageType.LOADING_MYSELF.ordinal
                    }
                    else -> {
                        MessageType.MYSELF.ordinal
                    }
                }
            }
            else -> MessageType.FOREIGN.ordinal
        }
    }

    fun submitList(messages: List<Message>) {
        asyncDiffer.submitList(messages)
    }

    fun sendMessage(message: Message, messageAddedWithLoadingUiCallback: () -> Unit) {
        val currentList = ArrayList(asyncDiffer.currentList)
        currentList.add(message)
        asyncDiffer.submitList(currentList, messageAddedWithLoadingUiCallback)
    }

    fun submitSentMessage(messages: List<Message>) {
        val currentList = asyncDiffer.currentList
        val firstNotSentMessageIndex =
            currentList.indexOfFirst { m -> m.id == Message.NOT_SENT_MESSAGE }
        val notReceivedSubList = currentList.subList(firstNotSentMessageIndex + 1, currentList.size)

        val updateMessages = ArrayList(messages)
        updateMessages.addAll(notReceivedSubList)
        asyncDiffer.submitList(messages)
    }

    class MessageViewHolder(
        itemView: View,
        private val reactionListener: (isReactionSelectedNow: Boolean, messageId: Long, emojiName: String) -> Unit,
        private val emojiDialogShower: (messageId: Long) -> Boolean
    ) : RecyclerView.ViewHolder(itemView) {

        private val messageLayout = itemView as IMessageLayout
        private val flexbox: FlexBoxLayout = itemView.findViewById(R.id.flexbox)
        private val messageView: TextView = itemView.findViewById(R.id.message)

        fun bind(message: Message, currentUserId: Long) {
            if (messageLayout is ForeignMessageLayout) {
                messageLayout.avatar = message.sender.avatarUrl
                messageLayout.name = message.sender.name
            }

            messageLayout.message = HtmlCompat.fromHtml(
                message.messageContent,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).removeSuffix("\n\n")

            messageView.setOnLongClickListener {
                return@setOnLongClickListener emojiDialogShower(message.id)
            }

            addReactions(message, currentUserId)

            flexbox.plusViewOnClickListener = { emojiDialogShower(message.id) }
        }

        private fun addReactions(
            message: Message,
            currentUserId: Long
        ) {
            flexbox.removeAllViews()
            for (reactionGroup in message.reactions.groupBy { it.emojiCode }.iterator()) {
                flexbox.addView(
                    ReactionView(itemView.context).apply {
                        val (emojiCode, userIdsOnReaction) = reactionGroup
                        emojiUnicode = emojiCode
                        count = userIdsOnReaction.size

                        isSelected = userIdsOnReaction.any { it.userId == currentUserId }

                        setOnClickListener {
                            val reaction = userIdsOnReaction.first()
                            reactionListener(isSelected, message.id, reaction.emojiName)
                            isSelected = !isSelected
                        }
                    }
                )
            }
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageLayout = itemView.findViewById(R.id.myself_message) as IMessageLayout

        fun bind(message: Message) {
            messageLayout.message =
                HtmlCompat.fromHtml(message.messageContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    .removeSuffix("\n\n")
        }
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
        MYSELF,
        LOADING_MYSELF
    }
}