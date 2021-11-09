package com.holdbetter.fintechchatproject.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.services.ContextExtensions.dpToPx
import kotlin.math.max

class FlexBoxLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    var plusViewOnClickListener: (() -> Unit)? = null,
) :
    ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val marginBtwItemsHorizontal = context?.dpToPx(10f) ?: 0
    private val marginBtwItemsVertical = context?.dpToPx(7f) ?: 0
    private var amountOfElementsInRow = ArrayList<Int>(childCount)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        amountOfElementsInRow.clear()
        amountOfElementsInRow.add(0)

        var usedWidth = 0
        var usedHeight = 0

        var maxRowWidth = 0

        if (childCount > 1) {
            usedWidth = paddingStart + paddingEnd
            usedHeight = paddingTop + paddingBottom
            var currentRow = 0
            val maxHeightsOfEachRow = arrayListOf(0)
            val maxPossibleWidth = MeasureSpec.getSize(widthMeasureSpec)
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


            usedHeight =
                maxHeightsOfEachRow.sum() + (maxHeightsOfEachRow.size - 1) * marginBtwItemsVertical
        }

        val summaryWidth = resolveSize(maxRowWidth, widthMeasureSpec)
        val summaryHeight = resolveSize(usedHeight, heightMeasureSpec)

        setMeasuredDimension(summaryWidth, summaryHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 1) {
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
                plusViewOnClickListener?.invoke()
            }
        })
    }

    private fun deleteImageView() {
        removeViewAt(childCount - 1)
    }
}