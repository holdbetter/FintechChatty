package com.holdbetter.fintechchatproject.app.chat.view.topic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.ui.IMessageLayout

class MessageSentInTopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun from(inflater: LayoutInflater, parent: ViewGroup): MessageSentInTopicViewHolder {
            val view = inflater.inflate(R.layout.myself_message_loading_in_topic_instance, parent, false)
            return MessageSentInTopicViewHolder(view)
        }
    }

    private val messageLayout = itemView.findViewById(R.id.message_layout) as IMessageLayout

    fun bind(message: MessageItem.Message) {
        messageLayout.message =
            HtmlCompat.fromHtml(message.messageContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
                .removeSuffix("\n\n")
    }
}