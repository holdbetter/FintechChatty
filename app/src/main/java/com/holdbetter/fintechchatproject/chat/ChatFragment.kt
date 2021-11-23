package com.holdbetter.fintechchatproject.chat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.viewmodel.PersonalViewModel
import com.holdbetter.fintechchatproject.chat.services.DateOnChatDecorator
import com.holdbetter.fintechchatproject.chat.services.ScrollLinearLayoutManager
import com.holdbetter.fintechchatproject.chat.view.ChatViewState
import com.holdbetter.fintechchatproject.chat.view.EmojiBottomModalFragment
import com.holdbetter.fintechchatproject.chat.view.IChatViewer
import com.holdbetter.fintechchatproject.chat.viewmodel.ChatViewModel
import com.holdbetter.fintechchatproject.domain.retrofit.Narrow
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toSender
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.Reaction
import java.util.*

class ChatFragment : Fragment(R.layout.fragment_chat), IChatViewer {
    companion object {
        const val TOPIC_NAME_KEY = "topic"
        const val STREAM_ID_KEY = "streamId"
        const val STREAM_NAME_KEY = "streamName"

        fun newInstance(streamId: Long, streamName: String, topicName: String): ChatFragment {
            return ChatFragment().apply {
                arguments = bundleOf(
                    STREAM_ID_KEY to streamId,
                    TOPIC_NAME_KEY to topicName,
                    STREAM_NAME_KEY to streamName
                )
            }
        }
    }

    private val chatViewModel: ChatViewModel by viewModels()
    private val personalViewModel: PersonalViewModel by activityViewModels()
//    private val emojiViewModel: EmojiViewModel by activityViewModels()

    private var streamId: Long? = null
    private var topicName: String? = null
    private var streamName: String? = null

    private var messageList: RecyclerView? = null
    private var progress: CircularProgressIndicator? = null
    private var inputMessage: EditText? = null
    private var chatActionButton: MaterialButton? = null
    private var hashtagToolbarTitle: MaterialToolbar? = null
    private var topicSubtitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        streamId = requireArguments().getLong(STREAM_ID_KEY)
        streamName = requireArguments().getString(STREAM_NAME_KEY)
        topicName = requireArguments().getString(TOPIC_NAME_KEY)

        childFragmentManager.setFragmentResultListener(EmojiBottomModalFragment.RESULT_REQUEST_KEY,
            this) { _, bundle ->
            val emojiName = bundle.getString(EmojiBottomModalFragment.EMOJI_SELECTED_NAME_KEY)
            val emojiCode = bundle.getString(EmojiBottomModalFragment.EMOJI_SELECTED_CODE_KEY)
            val messageId = bundle.getLong(EmojiBottomModalFragment.MESSAGE_ID_KEY)

//            chatViewModel.sendReaction(
//                emojiViewModel.originalEmojiList,
//                messageId,
//                Reaction(
//                    personalViewModel.currentUserId,
//                    emojiName!!,
//                    emojiCode!!
//                ),
//                streamId!!,
//                topicName!!
//            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewHierarchy(view)
        this.bind()
    }

    private fun bind() {
        chatViewModel.getMessages(
            personalViewModel::getMyself,
            Narrow.MessageNarrow(streamId!!, topicName!!)
        )

        chatViewModel.chatViewState.observe(viewLifecycleOwner, ::handleChatViewState)
        hashtagToolbarTitle?.title = "#${streamName!!}"
        topicSubtitle?.text = String.format("Topic: %s", topicName!!)
    }

    private fun initViewHierarchy(view: View) {
        messageList = view.findViewById<RecyclerView>(R.id.messages).apply {
            layoutManager = ScrollLinearLayoutManager(context)
            adapter = MessageAdapter(this@ChatFragment::onReactionPressed)
            addItemDecoration(DateOnChatDecorator(context))
        }

        progress = view.findViewById(R.id.progress)
        inputMessage = view.findViewById(R.id.input_message)
        chatActionButton = view.findViewById(R.id.chat_action_button)

        hashtagToolbarTitle = view.findViewById<MaterialToolbar>(R.id.chat_hashtag_title).apply {
            setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        topicSubtitle = view.findViewById(R.id.topic_name)

        val chatActionButton = view.findViewById<MaterialButton>(R.id.chat_action_button)
        val inputMessage = view.findViewById<EditText>(R.id.input_message).apply {
            doOnTextChanged { text, _, _, _ ->
                chatActionButton.isActivated = !text.isNullOrBlank()
            }
        }

        chatActionButton.setOnClickListener { sendTextButton ->
            if (sendTextButton.isActivated) {
                chatViewModel.sendMessage(streamId!!, topicName!!, inputMessage.text.toString())
                clearTextField(inputMessage)
            } else {
                showNotSupportedFeatureNotification(view)
            }
        }
    }

    private fun handleChatViewState(state: ChatViewState) {
        when (state) {
            is ChatViewState.Error -> handleError(state.error)
            ChatViewState.Loading -> startLoading()
            is ChatViewState.MessagesUpdate -> {
                stopLoading()
                setMessages(state.messages)
            }
            is ChatViewState.MessageSent -> onMessageSent(state.textMessage)
            is ChatViewState.MessageReceived -> onMessageReceived(state.messages)
            is ChatViewState.MessageSendError -> handleError(state.error)
        }
    }

    private fun handleError(error: Throwable) {
        Snackbar.make(requireView(), "Problems in reaction update", Snackbar.LENGTH_SHORT).show()
    }

    override fun startLoading() {
        inputMessage!!.isVisible = false
        chatActionButton!!.isVisible = false
        inputMessage!!.isVisible = false

        progress!!.isVisible = true
    }

    override fun stopLoading() {
        progress!!.isVisible = false

        inputMessage!!.isVisible = true
        chatActionButton!!.isVisible = true
        inputMessage!!.isVisible = true
    }

    private fun clearTextField(inputMessage: EditText) {
        inputMessage.text = null
    }

    private fun showNotSupportedFeatureNotification(view: View) {
        Toast.makeText(view.context,
            "Attaching files isn't ready yet",
            Toast.LENGTH_SHORT).show()
    }

    override fun setMessages(messages: List<Message>) {
        messageList?.let {
            (it.adapter as MessageAdapter).submitList(messages, personalViewModel.currentUserId)
        }
    }

    private fun onMessageSent(messageText: String) {
        val message = Message(Message.NOT_SENT_MESSAGE,
            personalViewModel.currentUser.value!!.toSender(),
            messageText,
            Calendar.getInstance().timeInMillis / 1000
        )

        messageList?.let {
            (it.adapter as MessageAdapter).sendMessage(message)
        }
    }

    private fun onMessageReceived(messages: List<Message>) {
        messageList?.let {
            (it.adapter as MessageAdapter).submitSentMessage(messages)
        }
    }

    private fun onReactionPressed(
        isReactionSelectedNow: Boolean,
        messageId: Long,
        emojiName: String,
        emojiCode: String,
    ) {
        val reactionToUpdate = Reaction(personalViewModel.currentUserId, emojiName, emojiCode)
//        if (isReactionSelectedNow) {
//            chatViewModel.removeReaction(emojiViewModel.originalEmojiList,
//                messageId,
//                reactionToUpdate,
//                streamId!!,
//                topicName!!)
//        } else {
//            chatViewModel.sendReaction(emojiViewModel.originalEmojiList,
//                messageId,
//                reactionToUpdate,
//                streamId!!,
//                topicName!!)
//        }
    }
}