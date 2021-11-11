package com.holdbetter.fintechchatproject.navigation.channels.view

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.navigation.channels.viewmodel.StreamViewModel

class SubbedStreamsFragment : Fragment(R.layout.fragment_streams_sub_or_not),
    IStreamCategoryFragment {
    companion object {
        private const val STREAMS_KEY = "streams"

        fun newInstance(): SubbedStreamsFragment {
            val bundle = Bundle()
            return SubbedStreamsFragment().apply {
                arguments = bundle
            }
        }
    }

    private var streamsList: RecyclerView? = null
    private var shimmerContent: ListView? = null
    var shimmer: ShimmerFrameLayout? = null

    private val viewModel: StreamViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        streamsList = view.findViewById(R.id.streams_list)
        shimmerContent = view.findViewById(R.id.shimmer_content)
        shimmer = view.findViewById(R.id.shimmer)

        shimmerContent!!.adapter = ShimmerPlaceholderStreamListAdapter(view.context)

        streamsList!!.apply {
            addItemDecoration(DividerItemDecoration(view.context,
                DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(view.context,
                    R.drawable.streams_divider_decoration)!!)
            })

            adapter = StreamAdapter(viewModel)
        }

        viewModel.streamViewState.observe(viewLifecycleOwner, ::handleStreamViewState)
    }

    private fun handleStreamViewState(streamViewState: StreamViewState) {
        when (streamViewState) {
            is StreamViewState.Error -> Snackbar.make(requireView(),
                "Shit happens",
                Snackbar.LENGTH_SHORT).show()
            StreamViewState.Loading -> startShimming()
            is StreamViewState.Result -> {
                stopShimming()
                setStreams(streamViewState.streams)
            }
        }
    }

    override fun setStreams(streams: List<HashtagStream>) {
        (streamsList!!.adapter as StreamAdapter).submitList(streams)
    }

    override fun startShimming() {
        shimmer!!.isVisible = true
        shimmer!!.startShimmer()
        streamsList!!.isVisible = false
    }

    override fun stopShimming() {
        shimmer!!.stopShimmer()
        shimmer!!.isVisible = false
        streamsList!!.isVisible = true
    }
}