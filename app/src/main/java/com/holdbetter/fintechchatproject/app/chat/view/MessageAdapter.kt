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
import com.holdbetter.fintechchatproject.model.MessageItem
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

    val messages: List<MessageItem>
        get() = asyncDiffer.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MessageType.PORTION_LOADING.ordinal -> HeaderViewHolder.from(inflater, parent)
            MessageType.LOADING_MYSELF.ordinal -> MessageSentViewHolder.from(inflater, parent)
            MessageType.FOREIGN.ordinal -> {
                MessageViewHolder.from(
                    inflater,
                    parent,
                    isMyselfMessage = false,
                    reactionListener,
                    emojiDialogShower
                )
            }
            else -> MessageViewHolder.from(
                inflater,
                parent,
                isMyselfMessage = true,
                reactionListener,
                emojiDialogShower
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when(payloads.isEmpty()) {
            true -> when (val message = asyncDiffer.currentList[position]) {
                is MessageItem.Message -> when (holder) {
                    is MessageViewHolder -> holder.bind(message, currentUserId)
                    is MessageSentViewHolder -> holder.bind(message)
                }
            }
            false -> when (val message = asyncDiffer.currentList[position]) {
                is MessageItem.Message -> when (holder) {
                    is MessageViewHolder -> holder.update(message, currentUserId)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    override fun getItemViewType(position: Int): Int {
        return when (val message = asyncDiffer.currentList[position]) {
            is MessageItem.HeaderMessage -> {
                if (position == 0) MessageType.PORTION_LOADING.ordinal
                else throw Exception("Loading element should be at start of list. Wrong position $position")
            }
            is MessageItem.Message -> {
                when (message.sender.id) {
                    currentUserId -> {
                        when (message.id) {
                            MessageItem.Message.NOT_SENT_MESSAGE -> MessageType.LOADING_MYSELF.ordinal
                            else -> MessageType.MYSELF.ordinal
                        }
                    }
                    else -> MessageType.FOREIGN.ordinal
                }
            }
        }
    }

    fun submitList(messages: List<MessageItem>, messagesSetCallback: () -> Unit = {}) {
        asyncDiffer.submitList(messages, messagesSetCallback)
    }

    fun sendMessage(message: MessageItem.Message, messageAddedWithLoadingUiCallback: () -> Unit) {
        val currentList = ArrayList(asyncDiffer.currentList)
        currentList.add(message)
        asyncDiffer.submitList(currentList, messageAddedWithLoadingUiCallback)
    }

    fun submitSentMessage(messageAddedCallback: () -> Unit = {}) {
        val currentList = asyncDiffer.currentList
        val firstNotSentMessage =
            currentList.first { m -> m.id == MessageItem.Message.NOT_SENT_MESSAGE } as MessageItem.Message
        val notReceivedSubList =
            currentList.subList(currentList.indexOf(firstNotSentMessage) + 1, currentList.size)

        val updatedList = listOf(
            *currentList.toTypedArray(),
            firstNotSentMessage.copy(id = MessageItem.Message.RECEIVED_MESSAGE),
            *notReceivedSubList.toTypedArray()
        )

        asyncDiffer.submitList(updatedList, messageAddedCallback)
    }

    class MessageViewHolder(
        itemView: View,
        private val reactionListener: (isReactionSelectedNow: Boolean, messageId: Long, emojiName: String) -> Unit,
        private val emojiDialogShower: (messageId: Long) -> Boolean
    ) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun from(
                inflater: LayoutInflater,
                parent: ViewGroup,
                isMyselfMessage: Boolean,
                reactionListener: (isReactionSelectedNow: Boolean, messageId: Long, emojiName: String) -> Unit,
                emojiDialogShower: (messageId: Long) -> Boolean
            ): MessageViewHolder {
                val view = if (isMyselfMessage) {
                    inflater.inflate(R.layout.myself_message_instance, parent, false)
                } else {
                    inflater.inflate(R.layout.foreign_message_instance, parent, false)
                }
                return MessageViewHolder(view, reactionListener, emojiDialogShower)
            }
        }

        private val messageLayout = itemView as IMessageLayout
        private val flexbox: FlexBoxLayout = itemView.findViewById(R.id.flexbox)
        private val messageView: TextView = itemView.findViewById(R.id.message)

        fun bind(message: MessageItem.Message, currentUserId: Long) {
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

        fun update(message: MessageItem.Message, currentUserId: Long) {
            addReactions(message, currentUserId)
        }

        private fun addReactions(
            message: MessageItem.Message,
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

            flexbox.addPlusView()
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun from(inflater: LayoutInflater, parent: ViewGroup): HeaderViewHolder {
                val view = inflater.inflate(R.layout.message_loading_header, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }

    class MessageSentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun from(inflater: LayoutInflater, parent: ViewGroup): MessageSentViewHolder {
                val view = inflater.inflate(R.layout.myself_message_loading_instance, parent, false)
                return MessageSentViewHolder(view)
            }
        }

        private val messageLayout = itemView.findViewById(R.id.myself_message) as IMessageLayout

        fun bind(message: MessageItem.Message) {
            messageLayout.message =
                HtmlCompat.fromHtml(message.messageContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    .removeSuffix("\n\n")
        }
    }

    class MessageDiffUtilCallback : DiffUtil.ItemCallback<MessageItem>() {
        override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: MessageItem, newItem: MessageItem): Any? {
            return when {
                oldItem is MessageItem.HeaderMessage || newItem is MessageItem.HeaderMessage -> {
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

    enum class MessageType {
        PORTION_LOADING,
        FOREIGN,
        MYSELF,
        LOADING_MYSELF
    }
}