package com.holdbetter.fintechchatproject.navigation.channels.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.holdbetter.fintechchatproject.R

class ShimmerPlaceholderListAdapter(context: Context) : BaseAdapter() {
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = 10

    override fun getItem(position: Int): Any { return Any() }

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return inflater.inflate(R.layout.shimmer_placeholder, parent, false)
    }
}