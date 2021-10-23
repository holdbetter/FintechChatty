package com.holdbetter.fragmentsandmessaging.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.holdbetter.fragmentsandmessaging.R
import com.holdbetter.fragmentsandmessaging.services.Util.dpToPx

class MySelfMessageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes), MessageLayout {
    companion object {
        const val RECT_MARGIN_TOP = 10f
        const val RECT_MARGIN_BOTTOM = 10f
        const val RECT_MARGIN_LEFT = 14f
        const val RECT_MARGIN_RIGHT = 9f

        const val RECT_MARGIN_RADIUS = 18f
    }

    private val messageIndex = 0
    private val flexBoxIndex = 1


    private val messageBoxPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    override var message: String = ""
        set(fullName) {
            (getChildAt(messageIndex) as TextView).text = fullName
            field = message
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureMyselfMessage(widthMeasureSpec, heightMeasureSpec)
    }

    private fun measureMyselfMessage(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        require(childCount == 2) {
            """Elements can have only 2 elements: Message (TextView), FlexBoxLayout            
                """.trimMargin()
                .replace('\n', ' ')
        }

        var usedHeight = 0

        val message = getChildAt(messageIndex) as TextView
        val flexBox = getChildAt(flexBoxIndex) as FlexBoxLayout

        measureChildWithMargins(message, widthMeasureSpec, 0, heightMeasureSpec, usedHeight)
        usedHeight += message.measuredHeight + message.marginTop + message.marginBottom

        val widthConstraint =
            MeasureSpec.getSize(widthMeasureSpec) - (message.measuredWidth + paddingLeft + paddingRight + context.dpToPx(
                RECT_MARGIN_LEFT + RECT_MARGIN_RIGHT))
        measureChildWithMargins(flexBox,
            widthMeasureSpec,
            widthConstraint,
            heightMeasureSpec,
            usedHeight)
        usedHeight += flexBox.measuredHeight + flexBox.marginTop + flexBox.marginBottom

        val summaryWidth =
            resolveSize(message.measuredWidth + paddingLeft + paddingRight + context.dpToPx(
                RECT_MARGIN_LEFT + RECT_MARGIN_RIGHT), widthMeasureSpec)
        val summaryHeight = resolveSize(usedHeight + paddingTop + paddingBottom,
            heightMeasureSpec) + context.dpToPx(RECT_MARGIN_TOP + RECT_MARGIN_BOTTOM)

        setMeasuredDimension(summaryWidth, summaryHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutMyselfMessage()
    }

    private fun layoutMyselfMessage() {
        val message = getChildAt(messageIndex) as TextView
        val flexBox = getChildAt(flexBoxIndex) as FlexBoxLayout

        message.layout(
            this.right - paddingRight - message.measuredWidth,
            paddingTop + message.marginTop + context.dpToPx(RECT_MARGIN_TOP),
            this.right - paddingRight,
            paddingTop + message.measuredHeight + message.marginTop + context.dpToPx(RECT_MARGIN_TOP)
        )

        flexBox.layout(
            message.left - context.dpToPx(RECT_MARGIN_LEFT),
            message.bottom + flexBox.marginTop + context.dpToPx(RECT_MARGIN_BOTTOM),
            this.right - paddingRight + context.dpToPx(RECT_MARGIN_RIGHT),
            message.bottom + flexBox.measuredHeight + flexBox.marginTop + context.dpToPx(
                RECT_MARGIN_BOTTOM)
        )
    }

    override fun dispatchDraw(canvas: Canvas?) {
        drawRectBehindMyselfMessage(canvas)
        super.dispatchDraw(canvas)
    }

    private fun drawRectBehindMyselfMessage(canvas: Canvas?) {
        val message = getChildAt(messageIndex) as TextView

        val l = message.left - context.dpToPx(RECT_MARGIN_LEFT)
        val r = message.right + context.dpToPx(RECT_MARGIN_RIGHT)
        val t = message.top - context.dpToPx(RECT_MARGIN_TOP)
        val b = message.bottom + context.dpToPx(RECT_MARGIN_BOTTOM)

        if (this.messageBoxPaint.shader == null) {
            this.messageBoxPaint.shader = getRightShader(r - l, t - b)
        }

        canvas?.drawRoundRect(l.toFloat(),
            t.toFloat(),
            r.toFloat(),
            b.toFloat(),
            context.dpToPx(RECT_MARGIN_RADIUS).toFloat(),
            context.dpToPx(RECT_MARGIN_RADIUS).toFloat(),
            messageBoxPaint)
    }

    private fun getRightShader(width: Int, height: Int): Shader {
        return LinearGradient(0f,
            0f,
            width.toFloat(),
            height * 4f,
            ContextCompat.getColor(this.context, R.color.message_box_gradient_start),
            ContextCompat.getColor(this.context, R.color.message_box_gradient_end),
            Shader.TileMode.MIRROR)

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

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }
}