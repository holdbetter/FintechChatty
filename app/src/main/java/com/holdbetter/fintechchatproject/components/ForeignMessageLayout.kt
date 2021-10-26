package com.holdbetter.fintechchatproject.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.services.ContextExtesions.dpToPx

class ForeignMessageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes), MessageLayout {
    companion object {

        const val RECT_MARGIN_TOP = 10f
        const val RECT_MARGIN_BOTTOM = 10f
        const val RECT_MARGIN_LEFT = 14f
        const val RECT_MARGIN_RIGHT = 29f

        const val RECT_MARGIN_RADIUS = 18f
    }

    private val avatarIndex = 0
    private val usernameIndex = 1
    private val messageIndex = 2
    private val flexBoxIndex = 3

    private val messageBoxPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(this@ForeignMessageLayout.context, R.color.black_matte)
    }

    var avatar: Int = 0
        set(avatarResourceId) {
            Glide.with(this)
                .load(avatarResourceId)
                .apply(RequestOptions().circleCrop())
                .into(getChildAt(avatarIndex) as ImageView)
            field = avatarResourceId
        }

    var name: String = ""
        set(fullName) {
            (getChildAt(usernameIndex) as TextView).text = fullName
            field = fullName
        }

    override var message: String = ""
        set(fullName) {
            (getChildAt(messageIndex) as TextView).text = fullName
            field = message
        }

    init {
        isActivated = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureForeignMessage(widthMeasureSpec, heightMeasureSpec)
    }

    private fun measureForeignMessage(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        require(childCount == 4) {
            """Elements can have only 4 elements in this case: Avatar (Image View),
                    |Username (TextView), Message (TextView), FlexBoxLayout            
                """.trimMargin()
                .replace('\n', ' ')
        }

        fun calculateRemainedWidth(
            username: TextView,
            message: TextView,
            flexBox: FlexBoxLayout,
        ): Int {
            return maxOf(
                maxOf(
                    username.measuredWidth + username.marginLeft + username.marginRight,
                    message.measuredWidth + message.marginLeft + message.marginRight
                ) + context.dpToPx(RECT_MARGIN_LEFT + RECT_MARGIN_RIGHT),
                flexBox.measuredWidth + flexBox.marginLeft + flexBox.marginRight
            )
        }

        var usedWidth = 0
        var usedHeight = 0

        val avatar = getChildAt(avatarIndex) as ImageView
        val username = getChildAt(usernameIndex) as TextView
        val message = getChildAt(messageIndex) as TextView
        val flexBox = getChildAt(flexBoxIndex) as FlexBoxLayout

        measureChildWithMargins(avatar, widthMeasureSpec, usedWidth, heightMeasureSpec, usedHeight)
        usedWidth += avatar.measuredWidth

        measureChildWithMargins(username,
            widthMeasureSpec,
            usedWidth,
            heightMeasureSpec,
            usedHeight)
        usedHeight += username.measuredHeight + username.marginTop + username.marginBottom

        measureChildWithMargins(message, widthMeasureSpec, usedWidth, heightMeasureSpec, usedHeight)
        usedHeight += message.measuredHeight + message.marginTop + message.marginBottom

        measureChildWithMargins(flexBox, widthMeasureSpec, usedWidth, heightMeasureSpec, usedHeight)
        usedHeight += flexBox.measuredHeight + flexBox.marginTop + flexBox.marginBottom

        usedWidth += calculateRemainedWidth(username, message, flexBox)

        val summaryWidth = resolveSize(usedWidth + paddingStart + paddingEnd, widthMeasureSpec)
        val summaryHeight = resolveSize(usedHeight + paddingTop + paddingBottom,
            heightMeasureSpec) + context.dpToPx(RECT_MARGIN_TOP + RECT_MARGIN_BOTTOM)

        setMeasuredDimension(summaryWidth, summaryHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutForeignMessage()
    }

    private fun layoutForeignMessage() {
        val avatar = getChildAt(avatarIndex) as ImageView
        val username = getChildAt(usernameIndex) as TextView
        val message = getChildAt(messageIndex) as TextView
        val flexBox = getChildAt(flexBoxIndex) as FlexBoxLayout

        avatar.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + avatar.measuredWidth,
            paddingTop + avatar.measuredHeight
        )

        username.layout(
            avatar.right + username.marginStart,
            paddingTop + context.dpToPx(RECT_MARGIN_TOP),
            avatar.right + username.measuredWidth + username.marginStart,
            paddingTop + username.measuredHeight + context.dpToPx(RECT_MARGIN_TOP)
        )

        message.layout(
            avatar.right + message.marginStart,
            username.bottom + message.marginTop,
            avatar.right + message.measuredWidth + message.marginStart,
            username.bottom + message.measuredHeight + message.marginTop
        )

        flexBox.layout(
            avatar.right + flexBox.marginStart,
            message.bottom + flexBox.marginTop + context.dpToPx(RECT_MARGIN_BOTTOM),
            avatar.right + flexBox.measuredWidth + flexBox.marginStart,
            message.bottom + flexBox.measuredHeight + flexBox.marginTop + context.dpToPx(
                RECT_MARGIN_BOTTOM)
        )
    }

    override fun dispatchDraw(canvas: Canvas?) {
        drawRectBehindForeignMessage(canvas)
        super.dispatchDraw(canvas)
    }

    private fun drawRectBehindForeignMessage(canvas: Canvas?) {
        val username = getChildAt(usernameIndex) as TextView
        val message = getChildAt(messageIndex) as TextView

        val l = username.left - context.dpToPx(RECT_MARGIN_LEFT)
        val r = maxOf(username.right, message.right) + context.dpToPx(RECT_MARGIN_RIGHT)
        val t = username.top - context.dpToPx(RECT_MARGIN_TOP)
        val b = message.bottom + context.dpToPx(RECT_MARGIN_BOTTOM)

        canvas?.drawRoundRect(l.toFloat(),
            t.toFloat(),
            r.toFloat(),
            b.toFloat(),
            context.dpToPx(RECT_MARGIN_RADIUS).toFloat(),
            context.dpToPx(RECT_MARGIN_RADIUS).toFloat(),
            messageBoxPaint)
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