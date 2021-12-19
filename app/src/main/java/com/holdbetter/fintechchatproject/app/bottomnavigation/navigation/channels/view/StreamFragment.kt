package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EdgeEffect
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.MainActivity
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.StreamEvent
import com.holdbetter.fintechchatproject.app.chat.ChatFragment
import com.holdbetter.fintechchatproject.databinding.FragmentStreamsSubOrNotBinding
import com.holdbetter.fintechchatproject.databinding.NoStreamInstanceBinding
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.model.Stream
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import com.holdbetter.fintechchatproject.services.FragmentExtensions.createStyledSnackbar
import vivid.money.elmslie.android.base.ElmFragment
import java.io.IOException

abstract class StreamFragment<Effect : Any, State : Any>(
    private val onErrorRetryEvent: StreamEvent
) :
    ElmFragment<StreamEvent, Effect, State>(R.layout.fragment_streams_sub_or_not), IStreamFragment {

    val binding by viewBinding(FragmentStreamsSubOrNotBinding::bind)
    val noStream by viewBinding(NoStreamInstanceBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            shimmerContent.adapter = ShimmerPlaceholderStreamListAdapter(view.context)

            streamsList.apply {
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

                edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                        return EdgeEffect(view.context).apply {
                            color = resources.getColor(R.color.green_accent_alpha, app.theme)
                        }
                    }
                }

                layoutManager = object : LinearLayoutManager(this.context, VERTICAL, false) {
                    override fun supportsPredictiveItemAnimations(): Boolean {
                        return false
                    }
                }

                adapter = StreamAdapter(::onTopicClicked)
            }
        }
    }

    override val initEvent: StreamEvent
        get() = StreamEvent.Ui.Init

    fun handleError(e: Throwable) {
        when (e) {
            is NotConnectedException, is IOException -> createStyledSnackbar(
                R.string.no_connection,
                actionTextId = R.string.try_again,
                action = { store.accept(onErrorRetryEvent) },
                length = Snackbar.LENGTH_INDEFINITE
            ).show()
        }
    }

    override fun setStreams(streams: List<Stream>) {
        (binding.streamsList.adapter as StreamAdapter).submitList(streams)
    }

    override fun shimming(turnOn: Boolean) {
        with(binding) {
            listShimmer.isVisible = turnOn
            if (turnOn) listShimmer.startShimmer() else listShimmer.stopShimmer()
            streamsList.isVisible = !turnOn
        }
    }

    override fun onTopicClicked(context: Context, topic: Topic) {
        val mainActivity = context as MainActivity
        val chatFragment =
            ChatFragment.newInstance(topic.streamId, topic.streamName, topic.name)

        mainActivity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_host_fragment, chatFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}