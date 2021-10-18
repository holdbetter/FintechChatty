package com.holdbetter.chatonrecycler.components

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.holdbetter.chatonrecycler.R
import com.holdbetter.chatonrecycler.services.Util.dpToPx
import kotlin.math.max

class FlexBoxLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) :
    ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        const val KEY_BASE_STATE = "base"
        const val KEY_DYNAMIC_BUNDLE = "dynamic"
        const val KEY_DYNAMIC_ELEMENTS_ID_LIST = "ids"
    }

    private val marginBtwItemsHorizontal = context?.dpToPx(10f) ?: 0
    private val marginBtwItemsVertical = context?.dpToPx(7f) ?: 0
    private var amountOfElementsInRow = ArrayList<Int>(childCount)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        amountOfElementsInRow.clear()
        amountOfElementsInRow.add(0)

        var usedWidth = paddingStart + paddingEnd
        var usedHeight = paddingTop + paddingBottom

        val maxPossibleWidth = MeasureSpec.getSize(widthMeasureSpec)
        var maxRowWidth = 0

        var currentRow = 0
        val maxHeightsOfEachRow = arrayListOf(0)
        children.forEach { child ->
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, usedHeight)

            if (usedWidth + child.measuredWidth > maxPossibleWidth) {
                currentRow += 1
                usedWidth = child.measuredWidth + marginBtwItemsHorizontal

                amountOfElementsInRow.add(0)
                maxHeightsOfEachRow.add(0)
            } else {
                usedWidth += child.measuredWidth + marginBtwItemsHorizontal
            }

            if (child.measuredHeight > maxHeightsOfEachRow[currentRow]) {
                maxHeightsOfEachRow[currentRow] = child.measuredHeight
            }

            amountOfElementsInRow[currentRow] += 1
            maxRowWidth = max(maxRowWidth, usedWidth)
        }


        usedHeight = maxHeightsOfEachRow.sum() + (maxHeightsOfEachRow.size - 1) * marginBtwItemsVertical
        val summaryWidth = resolveSize(maxRowWidth, widthMeasureSpec)
        val summaryHeight = resolveSize(usedHeight, heightMeasureSpec)

        setMeasuredDimension(summaryWidth, summaryHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var startX = 0
        var startY = 0
        var maxHeight = 0

        var currentRow = 0

        var elementsInRowToLayout = amountOfElementsInRow[currentRow]
        children.forEach { child ->
            if (elementsInRowToLayout == 0) {
                currentRow += 1
                startX = 0
                startY += maxHeight + marginBtwItemsVertical

                elementsInRowToLayout = amountOfElementsInRow[currentRow]
            }

            child.layout(startX,
                startY + paddingTop,
                startX + child.measuredWidth,
                startY + child.measuredHeight)
            maxHeight = max(maxHeight, child.measuredHeight)

            startX += child.width + marginBtwItemsHorizontal

            elementsInRowToLayout -= 1
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addImageView()
    }

    override fun onDetachedFromWindow() {
        deleteImageView()
        super.onDetachedFromWindow()
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()

        val baseParcel = super.onSaveInstanceState()
        bundle.putParcelable(KEY_BASE_STATE, baseParcel)

        val dynamicBundle = saveDynamicViewsState()
        bundle.putBundle(KEY_DYNAMIC_BUNDLE, dynamicBundle)

        return bundle
    }

    private fun saveDynamicViewsState(): Bundle {
        val dynamicBundle = Bundle()
        val dynamicViewsId = ArrayList<String>()
        for (child in children.toList()) {
            if (child is ReactionView && child.isDynamicallyAdded) {
                dynamicBundle.putParcelable(child.tag as String, child.onSaveInstanceState())
                dynamicViewsId.add(child.tag as String)
            }
        }
        dynamicBundle.putStringArrayList(KEY_DYNAMIC_ELEMENTS_ID_LIST, dynamicViewsId)
        return dynamicBundle
    }


    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        val base: Parcelable = bundle.getParcelable(KEY_BASE_STATE)!!
        super.onRestoreInstanceState(base)

        val dynamicBundle = bundle.getBundle(KEY_DYNAMIC_BUNDLE)
        restoreDynamicAddedView(dynamicBundle)
    }

    private fun restoreDynamicAddedView(bundle: Bundle?) {
        val idList = bundle?.getStringArrayList(KEY_DYNAMIC_ELEMENTS_ID_LIST)
        if (idList != null) {
            for (id in idList) {
                addView(ReactionView(context).apply {
                    onRestoreInstanceState(bundle.getParcelable(id))
                })
            }
        }
    }

    private fun addImageView() {
        addView(ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                context.dpToPx(46f),
                context.dpToPx(27.5f)
            )
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            background =
                ContextCompat.getDrawable(context, R.drawable.imageview_background_selector)
            setImageResource(R.drawable.ic_plus_reaction)
            setOnClickListener {
                addRandomReaction()
            }
        })
    }

    private fun deleteImageView() {
        removeViewAt(childCount - 1)
    }

    private fun addRandomReaction() {
        this@FlexBoxLayout.addView(
            ReactionView(context).apply {
                tag = "${generateViewId()}"
                isDynamicallyAdded = true
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            },
            childCount - 1,
        )
    }
}