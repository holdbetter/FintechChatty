package com.holdbetter.fintechchatproject.navigation.channels.view

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.navigation.channels.elm.stream.StreamEvent
import vivid.money.elmslie.android.base.ElmFragment
import java.io.IOException

abstract class StreamFragment<Effect : Any, State : Any>(
    private val onErrorRetryEvent: StreamEvent,
    @LayoutRes contentLayoutId: Int
) :
    ElmFragment<StreamEvent, Effect, State>(contentLayoutId), IStreamFragment {

    private var streamsList: RecyclerView? = null
    private var shimmerContent: ListView? = null
    var shimmer: ShimmerFrameLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        streamsList = view.findViewById(R.id.streams_list)
        shimmerContent = view.findViewById(R.id.shimmer_content)
        shimmer = view.findViewById(R.id.shimmer)

        shimmerContent!!.adapter = ShimmerPlaceholderStreamListAdapter(view.context)

        streamsList!!.apply {
            addItemDecoration(
                DividerItemDecoration(
                    view.context,
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(
                        ContextCompat.getDrawable(
                            view.context,
                            R.drawable.streams_divider_decoration
                        )!!
                    )
                })

            layoutManager = object : LinearLayoutManager(this.context, VERTICAL, false) {
                override fun supportsPredictiveItemAnimations(): Boolean {
                    return false
                }
            }

            adapter = StreamAdapter()
        }
    }

    override val initEvent: StreamEvent
        get() = StreamEvent.Ui.Init

    fun handleError(e: Throwable) {
        val appResource = resources
        val appTheme = requireActivity().theme

        val snackbar =
            Snackbar.make(requireView(), "Нет подключения к интернету", Snackbar.LENGTH_INDEFINITE)
                .apply {
                    setActionTextColor(appResource.getColor(R.color.blue_and_green, appTheme))
                    setTextColor(appResource.getColor(android.R.color.black, appTheme))
                    setBackgroundTint(appResource.getColor(R.color.white, appTheme))

                    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
                        .apply {
                            typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
                        }

                    setAction("Повторить") { store.accept(onErrorRetryEvent) }
                }
        when (e) {
            is NotConnectedException, is IOException -> snackbar.show()
        }
    }

    override fun setStreams(streams: List<Stream>) {
        (streamsList!!.adapter as StreamAdapter).submitList(streams)
    }

    override fun shimming(turnOn: Boolean) {
        shimmer?.isVisible = turnOn
        if (turnOn) shimmer?.startShimmer() else shimmer?.stopShimmer()
        streamsList?.isVisible = !turnOn
    }
}