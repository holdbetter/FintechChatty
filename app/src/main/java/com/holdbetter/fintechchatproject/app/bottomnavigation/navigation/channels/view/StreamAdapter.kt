package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.databinding.HashtagStreamInstanceBinding
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.model.Topic

class StreamAdapter(private val onTopicClicked: (Context, Topic) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    object DropdownAngle {
        const val TO_COLLAPSE = 0f
        const val TO_EXPAND = 180f
    }

    private val asyncDiffer = AsyncListDiffer(this, StreamDiffUtilCallback())

    init {
        submitList(emptyList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            StreamViewType.EMPTY_LIST.ordinal -> {
                EmptyViewHolder(
                    inflater.inflate(
                        R.layout.no_stream_instance,
                        parent,
                        false
                    )
                )
            }
            else -> {
                StreamViewHolder(
                    HashtagStreamInstanceBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onTopicClicked
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StreamViewHolder -> {
                val stream = asyncDiffer.currentList[position]
                holder.bind(stream)
            }
        }
        // TODO: 10/25/2021 Restoring expand state
    }

    override fun getItemCount() = if (asyncDiffer.currentList.isEmpty()) {
        1
    } else {
        asyncDiffer.currentList.size
    }

    override fun getItemViewType(position: Int): Int = if (asyncDiffer.currentList.isEmpty()) {
        StreamViewType.EMPTY_LIST.ordinal
    } else {
        StreamViewType.NON_EMPTY_LIST.ordinal
    }

    fun submitList(streams: List<Stream>) {
        asyncDiffer.submitList(streams)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class StreamViewHolder(
        private val binding: HashtagStreamInstanceBinding,
        onTopicClicked: (Context, Topic) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        // TODO: 11/2/2021 Animations support

        init {
            with(binding) {
                topicNestedList.apply {
                    addItemDecoration(
                        DividerItemDecoration(
                            itemView.context,
                            DividerItemDecoration.VERTICAL
                        ).apply {
                            setDrawable(
                                ContextCompat.getDrawable(
                                    itemView.context,
                                    R.drawable.alpha_recycler_decorator
                                )!!
                            )
                        }
                    )

                    layoutManager = object : LinearLayoutManager(this.context, VERTICAL, false) {
                        override fun supportsPredictiveItemAnimations(): Boolean {
                            return false
                        }
                    }

                    adapter = TopicAdapter(onTopicClicked)
                }
            }
        }

        fun bind(stream: Stream) {
            binding.streamName.text = stream.name
            submitTopics(stream.topics)
            setOnStreamClickListener(stream)
        }

        private fun setOnStreamClickListener(stream: Stream) {
            with(binding) {
                root.setOnClickListener { recyclerItem ->
                    if (!recyclerItem.isSelected) {
                        topicNestedList.isVisible = true
                        animateDropdownIcon(expand = true)
                        recyclerItem.isSelected = true
                        submitTopics(stream.topics)
                    } else {
                        topicNestedList.isVisible = false
                        animateDropdownIcon(expand = false)
                        recyclerItem.isSelected = false
                    }
                }
            }
        }

        private fun animateDropdownIcon(expand: Boolean) {
            val dropdownAnimator = binding.dropdown.animate()
            dropdownAnimator.duration = 200

            if (expand) {
                dropdownAnimator.rotation(DropdownAngle.TO_EXPAND)
            } else {
                dropdownAnimator.rotation(DropdownAngle.TO_COLLAPSE)
            }
        }

        private fun submitTopics(topics: List<Topic>) {
            (binding.topicNestedList.adapter as TopicAdapter).submitList(topics)
        }
    }

    class StreamDiffUtilCallback : DiffUtil.ItemCallback<Stream>() {
        override fun areItemsTheSame(oldItem: Stream, newItem: Stream): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Stream, newItem: Stream): Boolean {
            return oldItem == newItem
        }
    }

    enum class StreamViewType {
        EMPTY_LIST,
        NON_EMPTY_LIST
    }
}