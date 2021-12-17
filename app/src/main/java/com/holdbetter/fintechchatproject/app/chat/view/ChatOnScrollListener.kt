package com.holdbetter.fintechchatproject.app.chat.view

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.app.chat.elm.ChatEvent
import com.holdbetter.fintechchatproject.app.chat.elm.ChatStore

class ChatOnScrollListener(private val chatEdgeReachingAction: () -> Unit) : RecyclerView.OnScrollListener() {
    private var nextPageRequested: Boolean = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val linearManager = (recyclerView.layoutManager as LinearLayoutManager)
        val firstVisibleItem = linearManager.findFirstVisibleItemPosition()

        val updateItemPosition = 4

        if (firstVisibleItem < updateItemPosition && !nextPageRequested) {
            nextPageRequested = true
            chatEdgeReachingAction()
            recyclerView.removeOnScrollListener(this)
        }
    }
}