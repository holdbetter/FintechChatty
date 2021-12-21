package com.holdbetter.fintechchatproject.app.chat.stream.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.databinding.TopicChooserInstanceBinding
import com.holdbetter.fintechchatproject.model.TopicChooser

class TopicChooserAdapter(private val topicSelected: (topic: TopicChooser) -> Unit) :
    RecyclerView.Adapter<TopicChooserAdapter.TopicChooseViewHolder>() {

    private val topics: MutableList<TopicChooser> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicChooseViewHolder {
        return TopicChooseViewHolder(
            TopicChooserInstanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            topicSelected
        )
    }

    override fun onBindViewHolder(holder: TopicChooseViewHolder, position: Int) {
        val topic = topics[position]
        holder.bind(topic)
    }

    override fun getItemCount(): Int = topics.size

    fun clearSelections() {
        for (topic in topics) {
            topic.isSelected = false
        }
    }

    fun setTopics(topics: List<TopicChooser>) {
        this.topics.clear()
        this.topics.addAll(0, topics)
        notifyDataSetChanged()
    }

    fun getSelected(): TopicChooser? = topics.firstOrNull { it.isSelected }

    class TopicChooseViewHolder(
        private val binding: TopicChooserInstanceBinding,
        private val topicSelected: (topic: TopicChooser) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: TopicChooser) {
            with(binding) {
                chooseIndicator.clipToOutline = true
                chooseIndicator.isSelected = topic.isSelected
                topicName.text = topic.name

                chooseIndicator.setOnClickListener {
                    topicSelected(topic)
                }
            }
        }
    }
}