package com.holdbetter.fintechchatproject.app.chat.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class EmojiBottomModalFragment : BottomSheetDialogFragment(),
    IOnEmojiSelectedListener {
    companion object {
        const val MESSAGE_ID_KEY = "messageID"
        const val TAG = "EmojiBottomModalFragment"
        const val RESULT_REQUEST_KEY = "selectedEmoji"
        const val EMOJI_SELECTED_CODE_KEY = "emojiCode"
        const val EMOJI_SELECTED_NAME_KEY = "emojiName"

        fun newInstance(messageId: Long): EmojiBottomModalFragment {
            return EmojiBottomModalFragment().apply {
                arguments = bundleOf(
                    MESSAGE_ID_KEY to messageId,
                )
            }
        }
    }


    @Inject
    lateinit var emojiRepository: IEmojiRepository

    var messageId: Long by notNull()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        app.appComponent.inject(this)

        messageId = requireArguments().getLong(MESSAGE_ID_KEY)
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
        view.findViewById<RecyclerView>(R.id.emoji_list)!!.apply {
            layoutManager = GridLayoutManager(activity, 6)
            adapter = EmojiDialogAdapter(emojiRepository.cleanedEmojiList, this@EmojiBottomModalFragment)
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }
}