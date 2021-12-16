package com.holdbetter.fintechchatproject.app.chat.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.chat.EmojiDialogAdapter
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import javax.inject.Inject

class EmojiBottomModalFragment(private val messageId: Long) : BottomSheetDialogFragment(),
    IOnEmojiSelectedListener {
    @Inject
    lateinit var emojiRepository: IEmojiRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)

        app.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.emoji_bottom_dialog, container, false)
    }

    override fun finish(selectedEmojiName: String, selectedEmojiCode: String) {
        if (showsDialog) {
            dismiss()
            setFragmentResult(RESULT_REQUEST_KEY, Bundle().apply {
                putString(EMOJI_SELECTED_NAME_KEY, selectedEmojiName)
                putString(EMOJI_SELECTED_CODE_KEY, selectedEmojiCode)
                putLong(MESSAGE_ID_KEY, messageId)
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO: 10/19/2021 Auto span count
        // TODO: 10/19/2021 Offset btw items
        view.findViewById<RecyclerView>(R.id.emoji_list)!!.apply {
            layoutManager = GridLayoutManager(activity, 6)
            adapter = EmojiDialogAdapter(emojiRepository.cleanedEmojiList, this@EmojiBottomModalFragment)
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    companion object {
        const val TAG = "EmojiBottomModalFragment"
        const val RESULT_REQUEST_KEY = "selectedEmoji"
        const val EMOJI_SELECTED_CODE_KEY = "emojiCode"
        const val EMOJI_SELECTED_NAME_KEY = "emojiName"
        const val MESSAGE_ID_KEY = "messageId"
    }
}