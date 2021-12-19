package com.holdbetter.fintechchatproject.app.chat.services

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class ReverseLayoutManager(context: Context) : LinearLayoutManager(context, VERTICAL, false) {
    init {
        stackFromEnd = true
    }
}
