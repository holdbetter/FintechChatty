package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.databinding.TopicInstanceBinding
import com.holdbetter.fintechchatproject.model.Topic

class TopicAdapter(private val onTopicClicked: (Context, Topic) -> Unit) :
    RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {
    private val asyncDiffer = AsyncListDiffer(this, TopicDiffUtilCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TopicViewHolder(
            TopicInstanceBinding.inflate(
                inflater,
                parent,
                false
            ), onTopicClicked
        )
    }


    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(asyncDiffer.currentList[position])
    }

    override fun getItemCount() = asyncDiffer.currentList.size

    fun submitList(topic: List<Topic>) {
        asyncDiffer.submitList(topic)
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

    class TopicDiffUtilCallback : DiffUtil.ItemCallback<Topic>() {
        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem == newItem
        }
    }
}