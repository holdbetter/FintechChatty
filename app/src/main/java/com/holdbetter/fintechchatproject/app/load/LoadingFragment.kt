package com.holdbetter.fintechchatproject.app.load

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.bottomnavigation.NavigationFragment
import com.holdbetter.fintechchatproject.app.load.di.DaggerLoadingComponent
import com.holdbetter.fintechchatproject.app.load.elm.DataPrefetchEffect
import com.holdbetter.fintechchatproject.app.load.elm.DataPrefetchEvent
import com.holdbetter.fintechchatproject.app.load.elm.DataPrefetchState
import com.holdbetter.fintechchatproject.app.load.elm.DataPrefetchStore
import com.holdbetter.fintechchatproject.databinding.FragmentLoadingBinding
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import com.holdbetter.fintechchatproject.services.RxExtensions.delayEach
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.ReplaySubject
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoadingFragment :
    ElmFragment<DataPrefetchEvent, DataPrefetchEffect, DataPrefetchState>(R.layout.fragment_loading) {
    companion object {
        fun newInstance(): LoadingFragment {
            return LoadingFragment().apply {
                arguments = bundleOf()
            }
        }
    }

    @Inject
    lateinit var prefetchElmProvider: DataPrefetchStore

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var subject: ReplaySubject<Pair<View, AnimatorListenerAdapter>> =
        ReplaySubject.create()

    private val emptyAnimatorListenerAdapter
        get() = object : AnimatorListenerAdapter() {}

    private lateinit var viewsToAnimate: List<View>

    private val binding by viewBinding(FragmentLoadingBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val appComponent = app.appComponent
        DaggerLoadingComponent.factory().create(
            repositoryDependencies = appComponent
        ).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            failed.setOnClickListener { store.accept(DataPrefetchEvent.Ui.RetryClicked) }
            viewsToAnimate =
                listOf(emojiLoading.root, findingBugs.root, discoverYou.root, justOneMoreStep.root)

            emojiLoading.loadingState.setupLoadingViewState(
                R.drawable.turn_on_emoji_load_screen,
                R.string.emoji_loading
            )
            findingBugs.loadingState.setupLoadingViewState(
                R.drawable.no_bugs_load_screen,
                R.string.finding_bugs
            )
            discoverYou.loadingState.setupLoadingViewState(
                R.drawable.who_are_you_sized_load_screen,
                R.string.discover_you
            )
            justOneMoreStep.loadingState.setupLoadingViewState(
                R.drawable.one_more_step_load_screen,
                R.string.just_one_more_step
            )
            internetOn.loadingState.setupLoadingViewState(
                R.drawable.internet_sized_load_screen,
                R.string.internet_on
            )
        }
    }

    private fun TextView.setupLoadingViewState(
        @DrawableRes drawableLeftRes: Int,
        @StringRes textRes: Int
    ) {
        text = resources.getText(textRes)
        setCompoundDrawablesWithIntrinsicBounds(drawableLeftRes, 0, 0, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    override val initEvent: DataPrefetchEvent
        get() = DataPrefetchEvent.Ui.Started

    override fun createStore(): Store<DataPrefetchEvent, DataPrefetchEffect, DataPrefetchState> =
        prefetchElmProvider.provide()

    override fun render(state: DataPrefetchState) {}

    override fun handleEffect(effect: DataPrefetchEffect) {
        with(binding.internetOn) {
            when (effect) {
                DataPrefetchEffect.Started -> runAnimations()
                DataPrefetchEffect.Loaded -> pushSuccessView(root)
                is DataPrefetchEffect.ShowError -> {
                    frameContent.isEnabled = false
                    pushErrorView(root)
                }
                DataPrefetchEffect.StartNavigation -> goToNavigationFragment()
                DataPrefetchEffect.ReloadPage -> reloadPageState()
                DataPrefetchEffect.CleanUI -> cleanUI()
            }
        }
    }

    private fun runAnimations() {
        val uiBlockInterval = 1000L

        Observable.fromIterable(viewsToAnimate)
            .subscribeOn(Schedulers.io())
            .delayEach(uiBlockInterval, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { initAnimationHandler() }
            .subscribe { runFadeInAnimation(it, emptyAnimatorListenerAdapter) }
            .addTo(compositeDisposable)
    }

    private fun cleanUI() {
        with(binding) {
            val startedAlpha = 0f
            emojiLoading.root.alpha = startedAlpha
            findingBugs.root.alpha = startedAlpha
            discoverYou.root.alpha = startedAlpha
            justOneMoreStep.root.alpha = startedAlpha

            with(internetOn) {
                root.alpha = startedAlpha
                frameContent.isEnabled = true
            }

            failed.isVisible = false
        }
    }

    private fun pushSuccessView(view: View) {
        subject.onNext(view to object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                runCheckmarkAnimation()
            }
        })
    }

    private fun pushErrorView(view: View) {
        subject.onNext(view to object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                binding.failed.isVisible = true
            }
        })
    }

    private fun initAnimationHandler() {
        subject.delay(1300, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { (view, animatorListener) -> runFadeInAnimation(view, animatorListener) }
            .subscribe()
            .addTo(compositeDisposable)
    }

    private fun runCheckmarkAnimation() {
        val checkmark = binding.checkmark
        val animatorDuration = 1000L
        val animDrawable = checkmark.drawable
        val avd = animDrawable as AnimatedVectorDrawable
        checkmark.isVisible = true
        avd.start()

        postAnimationOver(animatorDuration)
    }

    private fun postAnimationOver(delayInMillis: Long) {
        Handler(Looper.getMainLooper())
            .postDelayed(
                { store.accept(DataPrefetchEvent.Ui.SuccessAnimationsOver) },
                delayInMillis
            )
    }

    private fun reloadPageState() {
        subject = ReplaySubject.create()
        store.accept(DataPrefetchEvent.Ui.Started)
    }

    private fun goToNavigationFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_host_fragment, NavigationFragment.newInstance(R.id.channels))
            .commitAllowingStateLoss()
    }

    private fun runFadeInAnimation(view: View, endCallback: AnimatorListenerAdapter) {
        val animation = view.animate()
            .alpha(1f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(800L)
            .setListener(endCallback)
        animation.start()
    }
}