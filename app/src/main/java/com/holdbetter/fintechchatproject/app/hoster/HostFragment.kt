package com.holdbetter.fintechchatproject.app.hoster

import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.di.PocketDI
import com.holdbetter.fintechchatproject.app.hoster.elm.CacheEffect
import com.holdbetter.fintechchatproject.app.hoster.elm.InitializerCacheEvent
import com.holdbetter.fintechchatproject.app.hoster.elm.InitializerCacheState
import com.holdbetter.fintechchatproject.app.load.LoadingFragment
import com.holdbetter.fintechchatproject.app.main.NavigationFragment
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store

class HostFragment : ElmFragment<InitializerCacheEvent, CacheEffect, InitializerCacheState>(R.layout.fragment_empty_hosting) {
    override val initEvent: InitializerCacheEvent
        get() = InitializerCacheEvent.Ui.Init

    override fun createStore(): Store<InitializerCacheEvent, CacheEffect, InitializerCacheState> = PocketDI.HostElmProvider.store.provide()

    override fun render(state: InitializerCacheState) {}

    override fun handleEffect(effect: CacheEffect) {
        when (effect) {
            CacheEffect.ContinueWithCache -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_host_fragment, NavigationFragment.newInstance(R.id.channels))
                    .commitAllowingStateLoss()
            }
            CacheEffect.GoOnline -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_host_fragment, LoadingFragment.newInstance())
                    .commitAllowingStateLoss()
            }
        }
    }
}