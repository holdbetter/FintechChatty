package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.chat.ChatFragment
import com.holdbetter.fintechchatproject.app.MainActivity
import com.holdbetter.fintechchatproject.model.Topic

class TopicAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var wasSomethingSubmitted: Boolean = false
    private val emptyListItemCount = 1
    private val asyncDiffer = AsyncListDiffer(this, TopicDiffUtilCallback())

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
            is TopicViewHolder -> holder.bind(asyncDiffer.currentList[position])
        }
    }

    override fun getItemCount() = when {
        asyncDiffer.currentList.size != 0 -> {
            asyncDiffer.currentList.size
        }
        wasSomethingSubmitted -> {
            emptyListItemCount
        }
        else -> {
            0
        }
    }

    fun submitList(topic: List<Topic>) {
        wasSomethingSubmitted = true
        asyncDiffer.submitList(topic)
    }

    override fun getItemViewType(position: Int) = if (asyncDiffer.currentList.size == 0) {
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

            itemView.setOnClickListener {
                navigateToChat(it.context, topic)
            }
        }

        private fun navigateToChat(
            context: Context,
            topic: Topic,
        ) {
            val mainActivity = context as MainActivity
            val chatFragment =
                ChatFragment.newInstance(topic.streamId, topic.streamName, topic.name)

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

    class TopicDiffUtilCallback : DiffUtil.ItemCallback<Topic>() {
        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem == newItem
        }
    }
}