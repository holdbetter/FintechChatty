package com.holdbetter.fintechchatproject.navigation.channels.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.navigation.channels.viewmodel.StreamViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

class StreamAdapter(val viewModel: StreamViewModel) :
    RecyclerView.Adapter<StreamAdapter.StreamViewHolder>() {
    object DropdownAngle {
        const val TO_COLLAPSE = 0f
        const val TO_EXPAND = 180f
    }

    private val asyncDiffer = AsyncListDiffer(this, StreamDiffUtilCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hashtag_stream_instance, parent, false)
        return StreamViewHolder(view)
    }

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        val stream = asyncDiffer.currentList[position]

        holder.stream.text = stream.name
        holder.setOnStreamClickListener(stream)

        // TODO: 10/25/2021 Restoring expand state
    }

    override fun getItemCount() = asyncDiffer.currentList.size

    fun submitList(streams: List<HashtagStream>) {
        asyncDiffer.submitList(streams)
    }

    inner class StreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: 11/2/2021 Animations support
        val stream: TextView = itemView.findViewById(R.id.stream_name)
        private val topicsRecycler: RecyclerView = itemView.findViewById(R.id.topic_nested_list)
        private val dropdown: ImageView = itemView.findViewById(R.id.dropdown)
        private val compositeDisposable = CompositeDisposable()

        init {
            topicsRecycler.apply {
                addItemDecoration(
                    DividerItemDecoration(itemView.context, DividerItemDecoration.VERTICAL).apply {
                        setDrawable(ContextCompat.getDrawable(itemView.context,
                            R.drawable.alpha_recycler_decorator)!!)
                    }
                )

                layoutManager = object : LinearLayoutManager(this.context, VERTICAL, false) {
                    override fun supportsPredictiveItemAnimations(): Boolean {
                        return false
                    }
                }
            }
        }

        private fun setInnerAdapter(stream: HashtagStream, topicsRecycler: RecyclerView) {
            topicsRecycler.adapter = TopicAdapter()
            this@StreamAdapter.viewModel.getTopics(stream)
                .subscribeBy(
                    onSuccess = {
                        (topicsRecycler.adapter as TopicAdapter).submitList(it)
                    }
                )
                .addTo(compositeDisposable)
        }

        private fun updateTopicsForStream(stream: HashtagStream, topicsRecycler: RecyclerView) {
            val adapter = topicsRecycler.adapter as TopicAdapter

            this@StreamAdapter.viewModel.getTopics(stream)
                .subscribeBy(
                    onSuccess = adapter::submitList
                )
                .addTo(compositeDisposable)
        }

        fun setOnStreamClickListener(stream: HashtagStream) {
            itemView.setOnClickListener { recyclerItem ->
                if (!recyclerItem.isSelected) {
                    if (topicsRecycler.adapter == null) {
                        setInnerAdapter(stream, topicsRecycler)
                    } else {
                        updateTopicsForStream(stream, topicsRecycler)
                    }

                    topicsRecycler.isVisible = true

                    animateDropdownIcon(expand = true)
                    recyclerItem.isSelected = true
                } else {
                    compositeDisposable.clear()
                    topicsRecycler.isVisible = false
                    animateDropdownIcon(expand = false)
                    recyclerItem.isSelected = false
                }
            }
        }

        private fun animateDropdownIcon(expand: Boolean) {
            val dropdownAnimator = dropdown.animate()
            dropdownAnimator.duration = 200

            if (expand) {
                dropdownAnimator.rotation(DropdownAngle.TO_EXPAND)
            } else {
                dropdownAnimator.rotation(DropdownAngle.TO_COLLAPSE)
            }
        }
    }

    class StreamDiffUtilCallback : DiffUtil.ItemCallback<HashtagStream>() {
        override fun areItemsTheSame(oldItem: HashtagStream, newItem: HashtagStream): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HashtagStream, newItem: HashtagStream): Boolean {
            return oldItem == newItem
        }
    }
}