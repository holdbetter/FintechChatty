package com.holdbetter.fintechchatproject.app.chat

import android.os.Bundle
import androidx.annotation.LayoutRes
import com.holdbetter.fintechchatproject.app.MainActivity
import com.holdbetter.fintechchatproject.app.chat.elm.ChatEffect
import com.holdbetter.fintechchatproject.app.chat.elm.ChatEvent
import com.holdbetter.fintechchatproject.app.chat.elm.ChatState
import com.holdbetter.fintechchatproject.app.chat.view.EmojiBottomModalFragment
import com.holdbetter.fintechchatproject.app.chat.view.IChatViewer
import com.holdbetter.fintechchatproject.model.MessageItem
import vivid.money.elmslie.android.base.ElmFragment

abstract class BaseChatFragment(
    @LayoutRes contentLayoutId: Int
) :
    ElmFragment<ChatEvent, ChatEffect, ChatState>(contentLayoutId),
    IChatViewer {

    val mainActivity
        get() = (requireActivity() as MainActivity)

    abstract val messages: List<MessageItem.Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.setFragmentResultListener(
            EmojiBottomModalFragment.RESULT_REQUEST_KEY,
            this
        ) { _, bundle ->
            val emojiName = bundle.getString(EmojiBottomModalFragment.EMOJI_SELECTED_NAME_KEY)!!
            val messageId = bundle.getLong(EmojiBottomModalFragment.MESSAGE_ID_KEY)

            store.accept(
                ChatEvent.Ui.ReactionSent(
                    messageId,
                    emojiName,
                    messages
                )
            )
        }
    }

    override fun onChatEdgeReaching() {
        store.accept(
            ChatEvent.Ui.TopLimitEdgeReached(
                messages.first().id,
                messages
            )
        )
    }

    override fun onMessageLongClicked(messageId: Long): Boolean {
        EmojiBottomModalFragment(messageId).show(
            childFragmentManager,
            EmojiBottomModalFragment.TAG
        )
        return true
    }

    override fun onReactionPressed(
        isReactionSelectedNow: Boolean,
        messageId: Long,
        emojiName: String
    ) {
        if (isReactionSelectedNow) {
            store.accept(
                ChatEvent.Ui.ReactionRemoved(
                    messageId,
                    emojiName,
                    messages
                )
            )
        } else {
            store.accept(
                ChatEvent.Ui.ReactionSent(
                    messageId,
                    emojiName,
                    messages
                )
            )
        }
    }
}