package com.holdbetter.fintechchatproject.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.main.view.EmojiState
import com.holdbetter.fintechchatproject.main.viewmodel.EmojiViewModel
import com.holdbetter.fintechchatproject.services.RxExtensions.delayEach
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class LoadingFragment : Fragment(R.layout.fragment_loading) {
    companion object {
        fun newInstance(): LoadingFragment {
            return LoadingFragment().apply {
                arguments = bundleOf()
            }
        }
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val emojiViewModel: EmojiViewModel by activityViewModels()

    private val subject: PublishSubject<View> = PublishSubject.create()

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

        failed.setOnClickListener {
            emojiViewModel.getEmojiList()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_host_fragment, newInstance())
                .commitAllowingStateLoss()
        }

        handleAnimation()

        viewsToAnimate = listOf(findingBugs, discoverYou, justOneMoreStep)

        getFadeInAnimator(emojiLoading).start()

        emojiViewModel.isEmojiLoaded.observe(viewLifecycleOwner, ::handleState)
    }

    private fun handleState(state: EmojiState) {
        when (state) {
            is EmojiState.Error -> {
                internetOnContent.isEnabled = false
                subject.onNext(internetOn)
            }
            EmojiState.Loaded -> subject.onNext(internetOn)
            EmojiState.Loading -> {
                for (view in viewsToAnimate) {
                    subject.onNext(view)
                }
            }
        }
    }

    private fun handleAnimation() {
        subject.delayEach(1600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapSingle { view ->
                Single.create<ViewPropertyAnimator> { emitter ->
                    val item = getRightAnimator(view)
                    emitter.onSuccess(item)
                }
            }
            .subscribe { it.start() }
            .addTo(compositeDisposable)
    }

    private fun getRightAnimator(view: View) = if (isItLastView(view)) {
        if (stateNotError()) {
            getFadeInAnimator(view)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        runCheckmarkAnimation()
                    }
                })
        } else {
            getFadeInAnimator(view)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        failed.isVisible = true
                    }
                })
        }
    } else {
        getFadeInAnimator(view)
    }

    private fun stateNotError(): Boolean {
        return internetOnContent.isEnabled
    }

    private fun isItLastView(view: View) = view.id == internetOn.id

    private fun runCheckmarkAnimation() {
        val animDrawable = checkmark.drawable
        val avd = animDrawable as AnimatedVectorDrawable
        checkmark.isVisible = true
        avd.start()

        navigateWithDelay()
    }

    private fun navigateWithDelay() {
        Handler(Looper.getMainLooper())
            .postDelayed({ navigateToNavigationFragment() }, 1000)
    }

    private fun navigateToNavigationFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_host_fragment, NavigationFragment.newInstance(R.id.channels))
            .commitAllowingStateLoss()
    }

    private fun getFadeInAnimator(view: View): ViewPropertyAnimator {
        return view.animate()
            .alpha(1f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(1000L)
    }
}