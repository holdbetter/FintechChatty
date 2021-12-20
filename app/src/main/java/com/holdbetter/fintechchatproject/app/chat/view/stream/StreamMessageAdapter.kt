package com.holdbetter.fintechchatproject.app.chat.view.stream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.app.chat.view.BaseMessageAdapter
import com.holdbetter.fintechchatproject.app.chat.view.HeaderViewHolder
import com.holdbetter.fintechchatproject.model.MessageItem

class StreamMessageAdapter(
    private val meId: Long,
    private val reactionListener: (
        isReactionSelectedNow: Boolean,
        messageId: Long,
        emojiName: String,
    ) -> Unit,
    private val emojiDialogShower: (messageId: Long) -> Boolean,
    private val onTopicClicked: (topicName: String) -> Unit
) : BaseMessageAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MessageType.PORTION_LOADING.ordinal -> HeaderViewHolder.from(inflater, parent)
            MessageType.LOADING_MYSELF.ordinal -> MessageSentInStreamViewHolder.from(
                inflater,
                parent
            )
            MessageType.FOREIGN.ordinal -> {
                MessageInStreamViewHolder.from(
                    inflater,
                    parent,
                    isMyselfMessage = false,
                    reactionListener,
                    emojiDialogShower,
                    onTopicClicked
                )
            }
            else -> MessageInStreamViewHolder.from(
                inflater,
                parent,
                isMyselfMessage = true,
                reactionListener,
                emojiDialogShower,
                onTopicClicked
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
                    is MessageInStreamViewHolder -> holder.bind(message, meId)
                    is MessageSentInStreamViewHolder -> holder.bind(message)
                }
            }
            false -> when (val message = asyncDiffer.currentList[position]) {
                is MessageItem.Message -> when (holder) {
                    is MessageInStreamViewHolder -> holder.update(message, meId)
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