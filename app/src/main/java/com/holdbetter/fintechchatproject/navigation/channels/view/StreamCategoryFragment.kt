package com.holdbetter.fintechchatproject.navigation.channels.view

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.HashtagStream

class StreamCategoryFragment : Fragment(R.layout.fragment_streams_sub_or_not) {
    companion object {
        private const val STREAMS_KEY = "streams"

        fun newInstance(streams: ArrayList<HashtagStream>): StreamCategoryFragment {
            val bundle = Bundle()
            bundle.putParcelableArrayList(STREAMS_KEY, streams)
            return StreamCategoryFragment().apply {
                arguments = bundle
            }
        }
    }

    var streamsList: RecyclerView? = null
    var shimmerContent: ListView? = null
    var shimmer: ShimmerFrameLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        streamsList = view.findViewById(R.id.streams_list)
        shimmerContent = view.findViewById(R.id.shimmer_content)
        shimmer = view.findViewById(R.id.shimmer)

        shimmerContent!!.adapter = ShimmerPlaceholderListAdapter(view.context)

        streamsList!!.apply {
            addItemDecoration(DividerItemDecoration(view.context,
                DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(view.context,
                    R.drawable.streams_divider_decoration)!!)
            })

            val streams = requireArguments().getParcelableArrayList<HashtagStream>(
                STREAMS_KEY)
            adapter = streams?.let { StreamAdapter(it) }
        }
    }
}