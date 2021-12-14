package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.databinding.TopicInstanceBinding
import com.holdbetter.fintechchatproject.model.Topic

class TopicAdapter(private val onTopicClicked: (Context, Topic) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var wasSomethingSubmitted: Boolean = false
    private val emptyListItemCount = 1
    private val asyncDiffer = AsyncListDiffer(this, TopicDiffUtilCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TopicViewType.TOPIC_INSTANCE.ordinal -> return TopicViewHolder(
                TopicInstanceBinding.inflate(
                    inflater,
                    parent,
                    false
                ), onTopicClicked
            )
            TopicViewType.NO_TOPICS_IN_THIS_STREAM.ordinal -> return EmptyViewHolder(
                inflater.inflate(
                    R.layout.no_topic_in_stream_instance,
                    parent,
                    false
                )
            )
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

    class TopicViewHolder(
        private val binding: TopicInstanceBinding,
        private val onTopicClicked: (Context, Topic) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: Topic) {
            with(binding) {
                topicName.text = topic.name
                root.setBackgroundColor(Color.parseColor(topic.color))

                root.setOnClickListener {
                    onTopicClicked(it.context, topic)
                }
            }
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