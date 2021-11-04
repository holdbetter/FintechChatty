package com.holdbetter.fintechchatproject.navigation.channels.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Topic

class StreamAdapter(var streams: MutableList<HashtagStream>) :
    RecyclerView.Adapter<StreamAdapter.StreamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hashtag_stream_instance, parent, false)
        return StreamViewHolder(view)
    }

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        val stream = streams[position]

        holder.stream.text = stream.name
        holder.addRecyclerItemClick(stream.topics)

        // TODO: 10/25/2021 Restoring expand state
    }

    override fun getItemCount() = streams.size

    fun submitList(streams: MutableList<HashtagStream>) {
        this.streams = streams
        notifyDataSetChanged()
    }

    class StreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: 11/2/2021 Animations support
//        private var topicRecyclerHeight: Int = 0

        val stream: TextView = itemView.findViewById(R.id.stream_name)
        private val topicsRecycler: RecyclerView = itemView.findViewById(R.id.topic_nested_list)
        private val dropdown: ImageView = itemView.findViewById(R.id.dropdown)

        init {
            topicsRecycler.apply {
                addItemDecoration(
                    DividerItemDecoration(itemView.context, DividerItemDecoration.VERTICAL).apply {
                        setDrawable(ContextCompat.getDrawable(itemView.context,
                            R.drawable.alpha_recycler_decorator)!!)
                    }
                )
            }
        }

        fun addRecyclerItemClick(topics: ArrayList<Topic>) {
            itemView.setOnClickListener { recyclerItem ->
                if (!recyclerItem.isSelected) {
                    topicsRecycler.adapter = TopicAdapter(topics)
//                    calculateAnimationsHeight()
                    topicsRecycler.isVisible = true

                    animateDropdownIcon(expand = true)
//                    expandTopics(expand = true, animate = true)
                    recyclerItem.isSelected = true
                } else {
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

        object DropdownAngle {
            const val TO_COLLAPSE = 0f
            const val TO_EXPAND = 180f
        }
    }
}