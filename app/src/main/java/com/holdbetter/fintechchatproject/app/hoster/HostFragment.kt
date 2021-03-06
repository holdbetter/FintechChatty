package com.holdbetter.fintechchatproject.app.hoster

import android.content.Context
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.NavigationFragment
import com.holdbetter.fintechchatproject.app.hoster.di.DaggerHostComponent
import com.holdbetter.fintechchatproject.app.hoster.elm.CacheEffect
import com.holdbetter.fintechchatproject.app.hoster.elm.HostStore
import com.holdbetter.fintechchatproject.app.hoster.elm.InitializerCacheEvent
import com.holdbetter.fintechchatproject.app.hoster.elm.InitializerCacheState
import com.holdbetter.fintechchatproject.app.load.LoadingFragment
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class HostFragment :
    ElmFragment<InitializerCacheEvent, CacheEffect, InitializerCacheState>(R.layout.fragment_empty_hosting) {
    @Inject
    lateinit var hostElmProvider: HostStore

    override fun onAttach(context: Context) {
        super.onAttach(context)

        DaggerHostComponent.factory()
            .create(repositoryDependencies = app.appComponent)
            .inject(this)
    }

    override val initEvent: InitializerCacheEvent
        get() = InitializerCacheEvent.Ui.Init

    override fun createStore(): Store<InitializerCacheEvent, CacheEffect, InitializerCacheState> =
        hostElmProvider.provide()

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