package com.holdbetter.fintechchatproject.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.services.ContextExtensions.dpToPx
import com.holdbetter.fintechchatproject.services.InvalidateNotNullEmoji
import com.holdbetter.fintechchatproject.services.RequestLayoutNotNullCount

@SuppressLint("ViewConstructor")
class ReactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttrs, defStyleRes) {
    companion object {
        const val DEFAULT_REACTION_COUNT = 0
        const val DEFAULT_TEXT_SIZE = 14f

        const val DEFAULT_TEXT_COLOR = R.color.text_gray
    }

    var emojiUnicode: String by InvalidateNotNullEmoji()

    var count: Int by RequestLayoutNotNullCount()

    private val content: String
        get() = if (emojiUnicode.isNotEmpty()) {
            "$emojiUnicode $count"
        } else {
            ""
        }

    private val coordinates = PointF()
    private val textBounds = Rect()
    private val fontMetrics = Paint.FontMetrics()
    private val defaultTypeface: Typeface = if (!isInEditMode) ResourcesCompat.getFont(context,
        R.font.inter_light)!! else Typeface.DEFAULT
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = context.dpToPx(DEFAULT_TEXT_SIZE).toFloat()
        color = ContextCompat.getColor(this@ReactionView.context, DEFAULT_TEXT_COLOR)
        textAlign = Paint.Align.CENTER
        typeface = defaultTypeface
    }

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ReactionView,
            defStyleAttrs,
            defStyleRes
        )

        val xmlEmojiCode = typedArray.getInt(R.styleable.ReactionView_emojiCode, -1)
        emojiUnicode = if (xmlEmojiCode != -1) xmlEmojiCode.toString() else emojiUnicode
        count = typedArray.getInt(R.styleable.ReactionView_count, DEFAULT_REACTION_COUNT)

        typedArray.recycle()

        background =
            ContextCompat.getDrawable(context, R.drawable.reaction_view_background_selector)
        setDefaultPadding()
    }

    private fun setDefaultPadding() {
        setPadding(
            context.dpToPx(10f),
            context.dpToPx(6f),
            context.dpToPx(10f),
            context.dpToPx(6f)
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(
            content,
            0,
            content.length,
            textBounds
        )

        val textHeight = textBounds.height()
        val textWidth = textBounds.width()

        val elementDesirableHeight = textHeight + paddingBottom + paddingTop
        val elementDesirableWidth = textWidth + paddingStart + paddingEnd

        val minWidth = context.dpToPx(46f)
        val minHeight = context.dpToPx(27.5f)

        val viewHeight = resolveSize(maxOf(elementDesirableHeight, minHeight), heightMeasureSpec)
        val viewWidth = resolveSize(maxOf(elementDesirableWidth, minWidth), heightMeasureSpec)

        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(
            content,
            coordinates.x,
            coordinates.y,
            textPaint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textPaint.getFontMetrics(fontMetrics)
        coordinates.x = w / 2f
        coordinates.y = h - fontMetrics.descent - paddingBottom
    }
}