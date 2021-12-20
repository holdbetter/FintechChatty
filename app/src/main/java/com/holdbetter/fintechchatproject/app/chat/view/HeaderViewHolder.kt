package com.holdbetter.fintechchatproject.app.chat.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun from(inflater: LayoutInflater, parent: ViewGroup): HeaderViewHolder {
            val view = inflater.inflate(R.layout.message_loading_header, parent, false)
            return HeaderViewHolder(view)
        }
    }
}