package com.holdbetter.fintechchatproject.app.load

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.di.PocketDI
import com.holdbetter.fintechchatproject.app.load.elm.EmojiLoadEffect
import com.holdbetter.fintechchatproject.app.load.elm.EmojiLoadEvent
import com.holdbetter.fintechchatproject.app.load.elm.EmojiLoadState
import com.holdbetter.fintechchatproject.app.main.NavigationFragment
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

class LoadingFragment :
    ElmFragment<EmojiLoadEvent, EmojiLoadEffect, EmojiLoadState>(R.layout.fragment_loading) {
    companion object {
        fun newInstance(): LoadingFragment {
            return LoadingFragment().apply {
                arguments = bundleOf()
            }
        }
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var subject: ReplaySubject<Pair<View, AnimatorListenerAdapter>> =
        ReplaySubject.create()

    private val emptyAnimatorListenerAdapter
        get() = object : AnimatorListenerAdapter() {}

    private lateinit var internetOn: MaterialCardView
    private lateinit var discoverYou: MaterialCardView
    private lateinit var emojiLoading: MaterialCardView
    private lateinit var findingBugs: MaterialCardView
    private lateinit var justOneMoreStep: MaterialCardView
    private lateinit var internetOnContent: FrameLayout
    private lateinit var failed: MaterialButton
    private lateinit var checkmark: ImageView

    private lateinit var viewsToAnimate: List<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        internetOn = view.findViewById(R.id.internetOn)
        discoverYou = view.findViewById(R.id.discoverYou)
        emojiLoading = view.findViewById(R.id.emojiLoading)
        findingBugs = view.findViewById(R.id.findingBugs)
        justOneMoreStep = view.findViewById(R.id.justOneMoreStep)
        internetOnContent = view.findViewById(R.id.internetOnContent)
        checkmark = view.findViewById(R.id.checkmark)
        failed = view.findViewById(R.id.failed)

        failed.setOnClickListener { store.accept(EmojiLoadEvent.Ui.RetryClicked) }

        viewsToAnimate = listOf(emojiLoading, findingBugs, discoverYou, justOneMoreStep)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    override val initEvent: EmojiLoadEvent
        get() = EmojiLoadEvent.Ui.Started

    override fun createStore(): Store<EmojiLoadEvent, EmojiLoadEffect, EmojiLoadState> =
        PocketDI.LoadingElmProvider.store.provide()

    override fun render(state: EmojiLoadState) {
    }

    override fun handleEffect(effect: EmojiLoadEffect) {
        when (effect) {
            EmojiLoadEffect.Started -> runAnimations()
            EmojiLoadEffect.Loaded -> pushSuccessView(internetOn)
            is EmojiLoadEffect.ShowError -> {
                internetOnContent.isEnabled = false
                pushErrorView(internetOn)
            }
            EmojiLoadEffect.StartNavigation -> goToNavigationFragment()
            EmojiLoadEffect.ReloadPage -> reloadPageState()
            EmojiLoadEffect.CleanUI -> cleanUI()
        }
    }

    private fun runAnimations() {
        Observable.fromIterable(viewsToAnimate)
            .subscribeOn(Schedulers.io())
            .delayEach(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { initAnimationHandler() }
            .subscribe { runFadeInAnimation(it, emptyAnimatorListenerAdapter) }
            .addTo(compositeDisposable)
    }

    private fun cleanUI() {
        val startedAlpha = 0f
        emojiLoading.alpha = startedAlpha
        findingBugs.alpha = startedAlpha
        discoverYou.alpha = startedAlpha
        justOneMoreStep.alpha = startedAlpha
        internetOn.alpha = startedAlpha

        internetOnContent.isEnabled = true

        failed.isVisible = false
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
                failed.isVisible = true
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
        val animatorDuration = 1000L
        val animDrawable = checkmark.drawable
        val avd = animDrawable as AnimatedVectorDrawable
        checkmark.isVisible = true
        avd.start()

        postAnimationOver(animatorDuration)
    }

    private fun postAnimationOver(delayInMillis: Long) {
        Handler(Looper.getMainLooper())
            .postDelayed({ store.accept(EmojiLoadEvent.Ui.SuccessAnimationsOver) }, delayInMillis)
    }

    private fun reloadPageState() {
        subject = ReplaySubject.create()
        store.accept(EmojiLoadEvent.Ui.Started)
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