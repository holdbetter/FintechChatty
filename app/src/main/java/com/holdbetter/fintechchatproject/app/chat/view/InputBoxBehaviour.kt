package com.holdbetter.fintechchatproject.app.chat.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

class InputBoxBehaviour(
    context: Context,
    attrs: AttributeSet
) : CoordinatorLayout.Behavior<LinearLayout>(context, attrs) {
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: LinearLayout,
        dependency: View
    ): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: LinearLayout,
        dependency: View
    ): Boolean {
        val y = dependency.translationY - dependency.height
        child.translationY = y
        return true
    }

    override fun onDependentViewRemoved(
        parent: CoordinatorLayout,
        child: LinearLayout,
        dependency: View
    ) {
        val y = child.translationY + dependency.height
        child.translationY = y
    }
}