package com.holdbetter.fintechchatproject.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holdbetter.fintechchatproject.R

class EmojiBottomModalFragment(private val messageId: Int) : BottomSheetDialogFragment(),
    IOnEmojiSelectedListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.emoji_bottom_dialog, container, false)
    }

    override fun finish(selectedEmojiSymbol: String) {
        if (showsDialog) {
            dismiss()
            val emojiUnicode = convertToBigUnicode(selectedEmojiSymbol)
            setFragmentResult(RESULT_REQUEST_KEY, Bundle().apply {
                putString(EMOJI_SELECTED_KEY, emojiUnicode)
                putInt(MESSAGE_ID_KEY, messageId)
            })
        }
    }

    private fun convertToBigUnicode(selectedEmojiText: String) =
        "U+${Integer.toHexString(selectedEmojiText.codePointAt(0))}".uppercase()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO: 10/19/2021 Auto span count
        // TODO: 10/19/2021 Offset btw items
        view.findViewById<RecyclerView>(R.id.emoji_list)!!.apply {
            layoutManager = GridLayoutManager(activity, 6)
            adapter = EmojiDialogAdapter(this@EmojiBottomModalFragment)
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    companion object {
        const val TAG = "EmojiBottomModalFragment"
        const val RESULT_REQUEST_KEY = "selectedEmoji"
        const val EMOJI_SELECTED_KEY = "emoji"
        const val MESSAGE_ID_KEY = "messageId"
    }


}

interface IOnEmojiSelectedListener {
    fun finish(selectedEmojiSymbol: String)
}