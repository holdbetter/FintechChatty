package com.holdbetter.fintechchatproject.app.chat.services

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context, VERTICAL, false) {
    init {
        stackFromEnd = true
    }

    override fun onItemsAdded(
        recyclerView: RecyclerView,
        positionStart: Int,
        itemCount: Int,
    ) {
        super.onItemsAdded(recyclerView, positionStart, itemCount)
        scrollToPosition(recyclerView.adapter!!.itemCount - 1)
    }
}
