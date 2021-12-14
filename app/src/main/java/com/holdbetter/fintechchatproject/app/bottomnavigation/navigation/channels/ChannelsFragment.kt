package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di.ChannelsComponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di.DaggerChannelsComponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel.ChannelModel
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.channel.ChannelStore
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.elm.stream.StreamEvent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view.StreamFragment
import com.holdbetter.fintechchatproject.databinding.FragmentChannelsBinding
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.room.services.UnexpectedRoomException
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import com.holdbetter.fintechchatproject.services.FragmentExtensions.createStyledSnackbar
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class ChannelsFragment :
    ElmFragment<ChannelModel.ChannelEvent,
            ChannelModel.ChannelEffect,
            ChannelModel.ChannelState>(R.layout.fragment_channels) {
    companion object {
        fun newInstance(): ChannelsFragment {
            val bundle = Bundle()
            return ChannelsFragment().apply {
                arguments = bundle
            }
        }
    }

    @Inject
    lateinit var channelsElmProvider: ChannelStore

    lateinit var channelComponent: ChannelsComponent

    private val binding by viewBinding(FragmentChannelsBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        with(app.appComponent) {
            channelComponent = DaggerChannelsComponent.factory().create(
                androidDependencies = this,
                domainDependencies = this,
                repositoryDependencies = this
            )
        }

        channelComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            with(swipeToRefresh) {
                isEnabled = false
                setColorSchemeResources(R.color.white)
                setProgressBackgroundColorSchemeResource(R.color.green_accent)
                setOnRefreshListener {
                    swipeToRefresh.isEnabled = false
                    refreshPage()
                }
            }

            with(channelPager) {
                offscreenPageLimit = 2
                adapter = ChannelPagerAdapter(this@ChannelsFragment)
            }

            TabLayoutMediator(channelTabs, channelPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.subscribed)
                    1 -> tab.text = getString(R.string.all_streams)
                }
            }.attach()

            streamSearchInput.doOnTextChanged { input, _, _, _ ->
                if (!input.isNullOrBlank() && channelPager.currentItem != 1) {
                    channelPager.currentItem = 1
                }

                store.accept(ChannelModel.ChannelEvent.Ui.Searching(input.toString()))
            }
        }
    }

    private fun notifyChildFragmentsAboutRefreshing() {
        val childFragmentItemCount = binding.channelPager.adapter!!.itemCount
        for (i in 0 until childFragmentItemCount) {
            (childFragmentManager.findFragmentByTag("f$i") as StreamFragment<*, *>)
                .storeHolder.store.accept(StreamEvent.Ui.Refreshing)
        }
    }

    override val initEvent: ChannelModel.ChannelEvent
        get() = ChannelModel.ChannelEvent.Ui.Started

    override fun createStore(): Store<ChannelModel.ChannelEvent, ChannelModel.ChannelEffect, ChannelModel.ChannelState> =
        channelsElmProvider.provide()

    override fun render(state: ChannelModel.ChannelState) {
        with(binding) {
            if (state.isDataLoaded) {
                streamSearchShimmer.stopShimmer()
                streamSearchShimmer.hideShimmer()
            } else {
                swipeToRefresh.isRefreshing = false
                streamSearchShimmer.startShimmer()
                streamSearchShimmer.showShimmer(true)
            }
        }
    }

    override fun handleEffect(effect: ChannelModel.ChannelEffect): Unit {
        binding.swipeToRefresh.isEnabled = true
        return when (effect) {
            ChannelModel.ChannelEffect.DataNotEmpty -> enableSearch()
            is ChannelModel.ChannelEffect.ShowError -> handleError(effect.error)
        }
    }

    private fun handleError(error: Throwable) {
        val snackbar = createStyledSnackbar()
        when (error) {
            is UnexpectedRoomException -> {
                snackbar.setText(R.string.unexpected_room_exception)
            }
            is IOException, is NotConnectedException -> {
                snackbar.setText(R.string.no_connection)
                snackbar.duration = Snackbar.LENGTH_LONG
                snackbar.setAction(R.string.try_again) { refreshPage() }
            }
            is TimeoutException -> {
                snackbar.setText(R.string.timeout_connection)
                snackbar.duration = Snackbar.LENGTH_LONG
                snackbar.setAction(R.string.try_again) { refreshPage() }
            }
            else -> {
                snackbar.setText(R.string.undefined_error_message)
                snackbar.duration = Snackbar.LENGTH_INDEFINITE
                snackbar.setAction(R.string.reload) { refreshPage() }
            }
        }

        snackbar.show()
    }

    private fun refreshPage() {
        notifyChildFragmentsAboutRefreshing()
        store.accept(ChannelModel.ChannelEvent.Ui.Retry)
    }

    private fun enableSearch() {
        with(binding) {
            streamSearchInput.isEnabled = true
            store.currentState.lastSearchRequest?.let {
                streamSearchInput.setText(it)
            }
        }
    }
}