package com.holdbetter.fintechchatproject.app.chat.view.stream

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.ui.IMessageLayout

class MessageSentInStreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun from(inflater: LayoutInflater, parent: ViewGroup): MessageSentInStreamViewHolder {
            val view = inflater.inflate(R.layout.myself_message_loading_in_stream_instance, parent, false)
            return MessageSentInStreamViewHolder(view)
        }
    }

    private val messageLayout = itemView.findViewById(R.id.message_layout) as IMessageLayout
    private val topic = itemView.findViewById(R.id.message_topic) as TextView

    fun bind(message: MessageItem.Message) {
        topic.text = message.topicName

        messageLayout.message =
            HtmlCompat.fromHtml(message.messageContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
                .removeSuffix("\n\n")
    }
}