package com.holdbetter.fintechchatproject.app.chat.view.stream

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.ui.FlexBoxLayout
import com.holdbetter.fintechchatproject.ui.ForeignMessageLayout
import com.holdbetter.fintechchatproject.ui.IMessageLayout
import com.holdbetter.fintechchatproject.ui.ReactionView

class MessageInStreamViewHolder(
    itemView: View,
    private val reactionListener: (isReactionSelectedNow: Boolean, messageId: Long, emojiName: String) -> Unit,
    private val emojiDialogShower: (messageId: Long) -> Boolean,
    private val onTopicClicked: (topicName: String) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun from(
            inflater: LayoutInflater,
            parent: ViewGroup,
            isMyselfMessage: Boolean,
            reactionListener: (isReactionSelectedNow: Boolean, messageId: Long, emojiName: String) -> Unit,
            emojiDialogShower: (messageId: Long) -> Boolean,
            onTopicClicked: (topicName: String) -> Unit
        ): MessageInStreamViewHolder {
            val view = if (isMyselfMessage) {
                inflater.inflate(R.layout.myself_message_in_stream_instance, parent, false)
            } else {
                inflater.inflate(R.layout.foreign_message_in_stream_instance, parent, false)
            }
            return MessageInStreamViewHolder(view, reactionListener, emojiDialogShower, onTopicClicked)
        }
    }

    private val messageLayout = itemView.findViewById(R.id.message_layout) as IMessageLayout
    private val flexbox: FlexBoxLayout = itemView.findViewById(R.id.flexbox)
    private val messageView: TextView = itemView.findViewById(R.id.message)
    private val topic: TextView = itemView.findViewById(R.id.message_topic)

    fun bind(message: MessageItem.Message, currentUserId: Long) {
        if (messageLayout is ForeignMessageLayout) {
            messageLayout.avatar = message.sender.avatarUrl
            messageLayout.name = message.sender.fullName
        }

        topic.text = message.topicName
        topic.setOnClickListener { onTopicClicked(message.topicName) }

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