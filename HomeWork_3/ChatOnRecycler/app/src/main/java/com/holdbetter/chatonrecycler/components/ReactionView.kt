package com.holdbetter.chatonrecycler.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.holdbetter.chatonrecycler.R
import com.holdbetter.chatonrecycler.services.InvalidateNotNullEmoji
import com.holdbetter.chatonrecycler.services.RequestLayoutNotNullCount
import com.holdbetter.chatonrecycler.services.Util.dpToPx
import com.holdbetter.chatonrecycler.services.Util.spToPx
import kotlinx.parcelize.Parcelize

class ReactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttrs, defStyleRes) {
    companion object {
        const val DEFAULT_EMOJI_UNICODE = 0x1F600
        const val DEFAULT_REACTION_COUNT = 0
        const val DEFAULT_TEXT_SIZE = 14f

        const val DEFAULT_TEXT_COLOR = R.color.reaction_count
    }

    private var _emojiUnicode: String
    var emojiUnicode: String by InvalidateNotNullEmoji()

    private var _count: Int
    var count: Int by RequestLayoutNotNullCount()

    var isDynamicallyAdded = false

    private val coordinates = PointF()
    private val textBounds = Rect()
    private val content: String
        get() = "${getEmojiByCode(emojiUnicode.substring(2).toInt(16))} $count"

    private val fontMetrics = Paint.FontMetrics()
    private val defaultTypeface: Typeface = if (!isInEditMode) ResourcesCompat.getFont(context,
        R.font.inter_light)!! else Typeface.DEFAULT
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = context.spToPx(DEFAULT_TEXT_SIZE).toFloat()
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

        _emojiUnicode = getEmojiByCode(typedArray.getInt(R.styleable.ReactionView_emojiCode,
            DEFAULT_EMOJI_UNICODE))
        _count = typedArray.getInt(R.styleable.ReactionView_count, DEFAULT_REACTION_COUNT)

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

        val viewHeight = resolveSize(elementDesirableHeight, heightMeasureSpec)
        val viewWidth = resolveSize(elementDesirableWidth, heightMeasureSpec)

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

    private fun getEmojiByCode(code: Int): String {
        return Character.toChars(code).concatToString()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            ACTION_DOWN -> return true
            ACTION_UP -> {
                isSelected = !isSelected
                if (isSelected) count++ else count--
                performClick()
                return true
            }
        }
        return false
    }

    public override fun onSaveInstanceState(): Parcelable {
        return ReactionSaveState(
            super.onSaveInstanceState(),
            this.emojiUnicode,
            this.count,
            this.isSelected,
            this.isDynamicallyAdded,
            this.tag as? String,
        )
    }

    public override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is ReactionSaveState) {
            emojiUnicode = state.emojiUnicode
            count = state.count
            isSelected = state.isSelected
            isDynamicallyAdded = state.isDynamicallyAdded
            tag = state.tag
        }
        super.onRestoreInstanceState(state)
    }

    fun incrementCount() {
        count++
    }

    @Parcelize
    class ReactionSaveState(
        private val parcelable: Parcelable?, // Parcelize annotation bug
        val emojiUnicode: String,
        val count: Int,
        val isSelected: Boolean,
        val isDynamicallyAdded: Boolean,
        val tag: String?,
    ) : BaseSavedState(parcelable)
}