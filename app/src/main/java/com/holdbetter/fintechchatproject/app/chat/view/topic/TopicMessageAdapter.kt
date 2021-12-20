package com.holdbetter.fintechchatproject.app.chat.view.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.app.chat.view.BaseMessageAdapter
import com.holdbetter.fintechchatproject.app.chat.view.HeaderViewHolder
import com.holdbetter.fintechchatproject.model.MessageItem

class TopicMessageAdapter(
    private val meId: Long,
    private val reactionListener: (
        isReactionSelectedNow: Boolean,
        messageId: Long,
        emojiName: String,
    ) -> Unit,
    private val emojiDialogShower: (messageId: Long) -> Boolean
) : BaseMessageAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MessageType.PORTION_LOADING.ordinal -> HeaderViewHolder.from(inflater, parent)
            MessageType.LOADING_MYSELF.ordinal -> MessageSentInTopicViewHolder.from(
                inflater,
                parent
            )
            MessageType.FOREIGN.ordinal -> {
                MessageInTopicViewHolder.from(
                    inflater,
                    parent,
                    isMyselfMessage = false,
                    reactionListener,
                    emojiDialogShower
                )
            }
            else -> MessageInTopicViewHolder.from(
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
        when (payloads.isEmpty()) {
            true -> when (val message = asyncDiffer.currentList[position]) {
                is MessageItem.Message -> when (holder) {
                    is MessageInTopicViewHolder -> holder.bind(message, meId)
                    is MessageSentInTopicViewHolder -> holder.bind(message)
                }
            }
            false -> when (val message = asyncDiffer.currentList[position]) {
                is MessageItem.Message -> when (holder) {
                    is MessageInTopicViewHolder -> holder.update(message, meId)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val message = asyncDiffer.currentList[position]) {
            is MessageItem.HeaderLoading -> {
                if (position == 0) MessageType.PORTION_LOADING.ordinal
                else throw Exception("Loading element should be at start of list. Wrong position $position")
            }
            is MessageItem.Message -> {
                when (message.sender.id) {
                    meId -> {
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
}