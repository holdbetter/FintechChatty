package com.holdbetter.fintechchatproject.fragment

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
import com.holdbetter.fintechchatproject.DateOnChatDecorator
import com.holdbetter.fintechchatproject.MessageAdapter
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.components.ScrollLinearLayoutManager
import com.holdbetter.fintechchatproject.model.IChatRepository
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.presenter.ITopicPresenter
import com.holdbetter.fintechchatproject.presenter.TopicPresenter
import com.holdbetter.fintechchatproject.services.FragmentExtensions.chatRepository
import com.holdbetter.fintechchatproject.view.ITopicViewer

class ChatFragment : Fragment(R.layout.fragment_chat), ITopicViewer {
    companion object {
        const val TOPIC_ID = "topic"

        fun newInstance(topicId: Int): ChatFragment {
            val bundle = Bundle()
            bundle.putInt(TOPIC_ID, topicId)
            return ChatFragment().apply {
                arguments = bundle
            }
        }
    }

    private var topicPresenter: ITopicPresenter? = null

    private var messageList: RecyclerView? = null
    private var hashtagToolbarTitle: MaterialToolbar? = null
    private var topicSubtitle: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val topicId = requireArguments().getInt(TOPIC_ID)
        topicPresenter = TopicPresenter(topicId, this.chatRepository, this)

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
                (messageList!!.adapter as MessageAdapter).addMessage(
                    Message(
                        chatRepository.users.first(),
                        inputMessage.text.toString(),
                        IChatRepository.TopicId.TESTING_ID
                    )
                )
                inputMessage.text = null
            } else {
                Toast.makeText(view.context,
                    "Attaching files isn't ready yet",
                    Toast.LENGTH_SHORT).show()
            }
        }

        topicPresenter!!.bind()
    }

    override fun setMessages(messages: ArrayList<Message>) {
        messageList?.adapter = MessageAdapter(messages)
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