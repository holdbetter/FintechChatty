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

class StreamAdapter(val viewModel: StreamViewModel) : RecyclerView.Adapter<StreamAdapter.StreamViewHolder>() {
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
        holder.setOnStreamClickListener(stream.id)

        // TODO: 10/25/2021 Restoring expand state
    }

    override fun getItemCount() = asyncDiffer.currentList.size

    fun submitList(streams: List<HashtagStream>) {
        asyncDiffer.submitList(streams)
    }

    inner class StreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: 11/2/2021 Animations support
//        private var topicRecyclerHeight: Int = 0
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

        private fun setInnerAdapter(streamId: Long, topicsRecycler: RecyclerView) {
            topicsRecycler.adapter = TopicAdapter()
            this@StreamAdapter.viewModel.getTopics(streamId)
                .subscribeBy(
                    onSuccess = {
                        (topicsRecycler.adapter as TopicAdapter).submitList(it)
                    }
                )
                .addTo(compositeDisposable)
        }

        private fun updateTopicsForStream(streamId: Long, topicsRecycler: RecyclerView) {
            val adapter = topicsRecycler.adapter as TopicAdapter

            this@StreamAdapter.viewModel.getTopics(streamId)
                .subscribeBy(
                    onSuccess = adapter::submitList
                )
                .addTo(compositeDisposable)
        }

        fun setOnStreamClickListener(streamId: Long) {
            itemView.setOnClickListener { recyclerItem ->
                if (!recyclerItem.isSelected) {
                    if (topicsRecycler.adapter == null) {
                        setInnerAdapter(streamId, topicsRecycler)
                    } else {
                        updateTopicsForStream(streamId, topicsRecycler)
                    }

//                    calculateAnimationsHeight()
                    topicsRecycler.isVisible = true

                    animateDropdownIcon(expand = true)
//                    expandTopics(expand = true, animate = true)
                    recyclerItem.isSelected = true
                } else {
                    compositeDisposable.clear()
                    topicsRecycler.isVisible = false
                    animateDropdownIcon(expand = false)
//                    expandTopics(expand = false, animate = true)
                    recyclerItem.isSelected = false
                }
            }
        }

//        private fun calculateAnimationsHeight() {
//            topicsRecycler.doOnPreDraw {
//                topicRecyclerHeight = it.height
//            }
//        }

        private fun animateDropdownIcon(expand: Boolean) {
            val dropdownAnimator = dropdown.animate()
            dropdownAnimator.duration = 200

            if (expand) {
                dropdownAnimator.rotation(DropdownAngle.TO_EXPAND)
            } else {
                dropdownAnimator.rotation(DropdownAngle.TO_COLLAPSE)
            }
        }

//        private fun expandTopics(expand: Boolean, animate: Boolean) {
//            if (animate) {
//                val animator: ValueAnimator = getValueAnimator(expand)
//
//                if (expand) {
//                    animator.doOnStart {
//                        topicsRecycler.isVisible = true
//                    }
//                } else {
//                    animator.doOnEnd {
//                        topicsRecycler.isVisible = false
//                    }
//                }
//
//                animator.start()
//            } else {
//                topicsRecycler.isVisible = expand
//                restoreRecyclerHeight(expand)
//            }
//        }
//
//        private fun restoreRecyclerHeight(expand: Boolean) {
//            if (expand) {
//                if (topicsRecycler.layoutParams.height != topicRecyclerHeight) {
//                    topicsRecycler.layoutParams.height = topicRecyclerHeight
//                }
//            }
//        }
//
//        private fun getValueAnimator(expand: Boolean) =
//            if (expand) animatorForward else animatorBackward
//
//        private val animatorForward = getValueAnimator(
//            true, 400, LinearInterpolator()
//        ) { progress ->
//            val h = (topicRecyclerHeight * progress).toInt()
//            topicsRecycler.layoutParams.height = h
//            topicsRecycler.requestLayout()
//        }
//
//        private val animatorBackward = getValueAnimator(
//            false, 150, LinearInterpolator()
//        ) { progress ->
//            topicsRecycler.layoutParams.height = (topicRecyclerHeight * progress).toInt()
//            topicsRecycler.requestLayout()
//        }
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