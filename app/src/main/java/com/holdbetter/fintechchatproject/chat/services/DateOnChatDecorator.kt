package com.holdbetter.fintechchatproject.chat.services

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.chat.MessageAdapter
import com.holdbetter.fintechchatproject.services.Util
import com.holdbetter.fintechchatproject.services.ContextExtesions.dpToPx
import com.holdbetter.fintechchatproject.services.ContextExtesions.spToPx
import java.util.*

class DateOnChatDecorator(context: Context) : DividerItemDecoration(context, LinearLayoutManager.VERTICAL) {
    private var dividerHeight: Int = 0
    private val backgroundHorizontalPadding = context.dpToPx(20f)
    private val backgroundVerticalPadding = context.dpToPx(8f)
    private val textBackground = ContextCompat.getDrawable(context,
        R.drawable.date_decoration_background)
    private val extraMarginTop = context.dpToPx(10f)

    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context,
            R.color.message_date_text)
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = context.spToPx(12f).toFloat()
        typeface = ResourcesCompat.getFont(context,
            R.font.inter_light)!!
    }

    private val dateText: String
        get() {
            val day = Util.calendarInstance.get(Calendar.DAY_OF_MONTH).toString()
            val month = Util.calendarInstance.getDisplayName(Calendar.MONTH,
                1,
                Locale("ru", "RU"))

            return "$day $month"
        }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter as MessageAdapter

        var currentDayDateInMillis: Long = 0
        for (childIndex in 0..parent.childCount) {
            val view = parent.getChildAt(childIndex)
            val position = parent.getChildAdapterPosition(view)

            if (position > -1) {
                val message = adapter.messages[position]

                Util.calendarInstance.clear()
                Util.calendarInstance.timeInMillis = message.dateInMillis

                val d = Util.calendarInstance.get(Calendar.DAY_OF_MONTH)
                val m = Util.calendarInstance.get(Calendar.MONTH)
                val y = Util.calendarInstance.get(Calendar.YEAR)

                Util.calendarInstance.clear()
                Util.calendarInstance.set(y, m, d)

                if (currentDayDateInMillis != Util.calendarInstance.timeInMillis) {
                    currentDayDateInMillis = Util.calendarInstance.timeInMillis

                    val textBounds = Rect()
                    textPaint.getTextBounds(dateText, 0, dateText.length, textBounds)

                    textBackground?.bounds = Rect(
                        parent.width / 2 - textBounds.width() / 2 - backgroundHorizontalPadding,
                        view.top - textBounds.height() - backgroundVerticalPadding,
                        parent.width / 2 + textBounds.width() / 2 + backgroundHorizontalPadding,
                        view.top
                    )

                    textBackground?.draw(c)

                    c.drawText(dateText,
                        parent.width / 2f,
                        view.top - (backgroundVerticalPadding / 2f),
                        textPaint)

                    dividerHeight = textBackground!!.bounds.height()
                }
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val textBounds = Rect()
        textPaint.getTextBounds(dateText, 0, dateText.length, textBounds)
        val dividerHeight = textBounds.height() + backgroundVerticalPadding
        outRect.top = dividerHeight + extraMarginTop
    }
}