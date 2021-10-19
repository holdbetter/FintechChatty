package com.holdbetter.chatonrecycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.ref.WeakReference

class EmojiBottomModalFragment(private val viewBoxDelivery: WeakReference<View>) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.emoji_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO: 10/19/2021 Auto span count
        // TODO: 10/19/2021 Offset btw items
        view.findViewById<RecyclerView>(R.id.emoji_list)!!.apply {
            layoutManager = GridLayoutManager(activity, 6)
            adapter = EmojiDialogAdapter(viewBoxDelivery, this@EmojiBottomModalFragment)
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    companion object {
        const val TAG = "EmojiBottomModalFragment"
    }
}