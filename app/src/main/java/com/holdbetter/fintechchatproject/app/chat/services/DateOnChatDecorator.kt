package com.holdbetter.fintechchatproject.app.chat.services

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
import com.holdbetter.fintechchatproject.app.chat.view.MessageAdapter
import com.holdbetter.fintechchatproject.services.ContextExtensions.dpToPx
import com.holdbetter.fintechchatproject.services.ContextExtensions.spToPx
import java.util.*

class DateOnChatDecorator(context: Context) :
    DividerItemDecoration(context, LinearLayoutManager.VERTICAL) {
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

    private var dateText: String = SAMPLE_DATA

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter as MessageAdapter

        val calendar = Calendar.getInstance()
        for (childIndex in 0..parent.childCount) {
            val view = parent.getChildAt(childIndex)
            val position = parent.getChildAdapterPosition(view)

            if (position > -1) {
                val message = adapter.messages[position]

                calendar.timeInMillis = message.dateInSeconds * 1000L
                val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
                val month = calendar.getDisplayName(Calendar.MONTH,
                    1,
                    Locale("ru", "RU"))

                dateText = "$day $month"

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

    companion object {
        const val SAMPLE_DATA = "9 Nov"
    }
}