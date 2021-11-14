package com.holdbetter.fintechchatproject.navigation.channels.view

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.main.ChatApplication
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.navigation.channels.viewmodel.StreamViewModel
import com.holdbetter.fintechchatproject.navigation.channels.viewmodel.StreamViewModelFactory
import com.holdbetter.fintechchatproject.services.FragmentExtensions.application
import java.io.IOException
import java.net.UnknownHostException

class AllStreamsFragment : Fragment(R.layout.fragment_streams_sub_or_not),
    IStreamCategoryFragment {
    companion object {
        private const val STREAMS_KEY = "streams"

        fun newInstance(): AllStreamsFragment {
            val bundle = Bundle()
            return AllStreamsFragment().apply {
                arguments = bundle
            }
        }
    }

    private var streamsList: RecyclerView? = null
    private var shimmerContent: ListView? = null
    private var shimmer: ShimmerFrameLayout? = null

    private val viewModel: StreamViewModel by activityViewModels()

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

            layoutManager = object : LinearLayoutManager(this.context, VERTICAL, false) {
                override fun supportsPredictiveItemAnimations(): Boolean {
                    return false
                }
            }

            adapter = StreamAdapter(viewModel)
        }

        viewModel.streamViewState.observe(viewLifecycleOwner, ::handleStreamViewState)
    }

    private fun handleStreamViewState(streamViewState: StreamViewState) {
        when (streamViewState) {
            is StreamViewState.Error -> {
                stopShimming()
                handleError(streamViewState.error)
            }
            StreamViewState.Loading -> startShimming()
            is StreamViewState.Result -> {
                stopShimming()
                setStreams(streamViewState.streams)
            }
            is StreamViewState.CacheShowing -> {
                stopShimming()
                setStreams(streamViewState.streams)
            }
        }
    }

    private fun handleError(e: Throwable) {
        val appResource = resources
        val appTheme = requireActivity().theme

        val snackbar = Snackbar.make(requireView() , "Нет подключения к интернету", Snackbar.LENGTH_INDEFINITE).apply {
            setActionTextColor(appResource.getColor(R.color.blue_and_green, appTheme))
            setTextColor(appResource.getColor(android.R.color.black, appTheme))
            setBackgroundTint(appResource.getColor(R.color.white, appTheme))

            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).apply {
                typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
            }

            setAction("Повторить") { viewModel.getStreams() }
        }

        when(e) {
            is NotConnectedException, is IOException -> snackbar.show()
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