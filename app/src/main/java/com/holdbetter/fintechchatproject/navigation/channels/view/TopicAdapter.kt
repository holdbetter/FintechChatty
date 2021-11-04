package com.holdbetter.fintechchatproject.navigation.channels.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.MainActivity
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.chat.ChatFragment
import com.holdbetter.fintechchatproject.model.Topic

class TopicAdapter(private val topics: ArrayList<Topic>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val emptyListItemCount = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TopicViewType.TOPIC_INSTANCE.ordinal -> return TopicViewHolder(inflater.inflate(R.layout.topic_instance,
                parent,
                false))
            TopicViewType.NO_TOPICS_IN_THIS_STREAM.ordinal -> return EmptyViewHolder(inflater.inflate(
                R.layout.no_topic_in_stream_instance,
                parent,
                false))
        }
        throw Exception("No corresponding viewType for $viewType value")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TopicViewHolder -> holder.bind(topics[position])
        }
    }

    override fun getItemCount() = if (topics.size != 0) {
        topics.size
    } else {
        emptyListItemCount
    }

    override fun getItemViewType(position: Int) = if (topics.size == 0) {
        TopicViewType.NO_TOPICS_IN_THIS_STREAM.ordinal
    } else {
        TopicViewType.TOPIC_INSTANCE.ordinal
    }

    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.topic_name)
        val count: TextView = itemView.findViewById(R.id.topic_messages_count)

        fun bind(topic: Topic) {
            name.text = topic.name
            itemView.setBackgroundColor(Color.parseColor(topic.color))
            count.text = String.format("%d mes", topic.messages.size)

            itemView.setOnClickListener {
                navigateToChat(it.context, topic)
            }
        }

        private fun navigateToChat(
            context: Context,
            topic: Topic,
        ) {
            val mainActivity = context as MainActivity
            val chatFragment = ChatFragment.newInstance(topic.id)

            mainActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_host_fragment, chatFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    enum class TopicViewType {
        NO_TOPICS_IN_THIS_STREAM,
        TOPIC_INSTANCE
    }
}