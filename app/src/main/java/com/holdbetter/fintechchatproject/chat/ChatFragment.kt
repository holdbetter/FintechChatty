package com.holdbetter.fintechchatproject.chat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.chat.services.ScrollLinearLayoutManager
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.chat.presenter.ITopicPresenter
import com.holdbetter.fintechchatproject.chat.presenter.TopicPresenter
import com.holdbetter.fintechchatproject.chat.services.DateOnChatDecorator
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository
import com.holdbetter.fintechchatproject.chat.view.ITopicViewer

class ChatFragment : Fragment(R.layout.fragment_chat), ITopicViewer {
    companion object {
        const val TOPIC_ID_KEY = "topic"

        fun newInstance(topicId: Int): ChatFragment {
            val bundle = Bundle()
            bundle.putInt(TOPIC_ID_KEY, topicId)
            return ChatFragment().apply {
                arguments = bundle
            }
        }
    }

    private var topicPresenter: ITopicPresenter? = null

    private var messageList: RecyclerView? = null
    private var hashtagToolbarTitle: MaterialToolbar? = null
    private var topicSubtitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val topicId = requireArguments().getInt(TOPIC_ID_KEY)
        topicPresenter = TopicPresenter(topicId, this.chatRepository, this)

        childFragmentManager.setFragmentResultListener(EmojiBottomModalFragment.RESULT_REQUEST_KEY,
            this) { _, bundle ->
            val emojiCode = bundle.getString(EmojiBottomModalFragment.EMOJI_SELECTED_KEY)
            val messageId = bundle.getInt(EmojiBottomModalFragment.MESSAGE_ID_KEY)

            topicPresenter!!.addReactionUsingDialog(messageId, emojiCode!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewHierarchy(view)
        topicPresenter!!.bind()
    }

    private fun initViewHierarchy(view: View) {
        messageList = view.findViewById<RecyclerView>(R.id.messages).apply {
            layoutManager = ScrollLinearLayoutManager(view.context)
            addItemDecoration(DateOnChatDecorator(view.context))
        }

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
                topicPresenter!!.addMessage(inputMessage.text.toString())
                clearTextField(inputMessage)
            } else {
                showNotSupportedFeatureNotification(view)
            }
        }
    }

    private fun clearTextField(inputMessage: EditText) {
        inputMessage.text = null
    }

    private fun showNotSupportedFeatureNotification(view: View) {
        Toast.makeText(view.context,
            "Attaching files isn't ready yet",
            Toast.LENGTH_SHORT).show()
    }

    override fun onReactionUpdated(messageId: Int) {
        val adapter = (messageList!!.adapter as MessageAdapter)
        adapter.updateMessage(messageId)
    }

    override fun onMessageInserted(position: Int) {
        messageList!!.adapter!!.notifyItemInserted(position)
    }

    override fun setMessages(messages: ArrayList<Message>) {
        messageList?.adapter = MessageAdapter(
            messages,
            topicPresenter!!::updateReaction
        )
    }

    override fun setTopicName(name: String) {
        topicSubtitle?.text = name
    }

    override fun setHashtagTitle(hashtag: String) {
        hashtagToolbarTitle?.title = hashtag
    }

    override fun onDestroyView() {
        topicPresenter!!.unbind()
        super.onDestroyView()
    }
}