package com.holdbetter.fintechchatproject.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.services.ContextExtensions.dpToPx

class MarginItemDecoration(private val marginInDp: Float) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val marginToSet = view.context.dpToPx(this.marginInDp)

        with(outRect) {
            bottom = marginToSet
        }
    }
}