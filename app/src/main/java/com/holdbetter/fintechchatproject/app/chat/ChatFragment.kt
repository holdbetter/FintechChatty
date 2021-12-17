package com.holdbetter.fintechchatproject.app.chat

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.chat.di.DaggerChatComponent
import com.holdbetter.fintechchatproject.app.chat.elm.*
import com.holdbetter.fintechchatproject.app.chat.services.DateOnChatDecorator
import com.holdbetter.fintechchatproject.app.chat.services.ScrollLinearLayoutManager
import com.holdbetter.fintechchatproject.app.chat.view.EmojiBottomModalFragment
import com.holdbetter.fintechchatproject.app.chat.view.IChatViewer
import com.holdbetter.fintechchatproject.app.chat.view.MessageAdapter
import com.holdbetter.fintechchatproject.databinding.FragmentChatBinding
import com.holdbetter.fintechchatproject.domain.repository.ChatRepositoryFactory
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toSender
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class ChatFragment : ElmFragment<ChatEvent, ChatEffect, ChatState>(R.layout.fragment_chat),
    IChatViewer {
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

    @Inject
    lateinit var personalRepository: IPersonalRepository
    @Inject
    lateinit var emojiRepository: IEmojiRepository
    @Inject
    lateinit var chatRepositoryFactory: ChatRepositoryFactory
    @Inject
    lateinit var chatActorFactory: ChatActorFactory
    @Inject
    lateinit var chatStoreFactory: ChatStoreFactory

    lateinit var chatStore: ChatStore

    val binding by viewBinding(FragmentChatBinding::bind)

    var streamId: Long by notNull()
    lateinit var topicName: String
    lateinit var streamName: String

    override fun onAttach(context: Context) {
        super.onAttach(context)

        with(requireArguments()) {
            streamId = getLong(STREAM_ID_KEY)
            streamName = getString(STREAM_NAME_KEY)!!
            topicName = getString(TOPIC_NAME_KEY)!!
        }

        with(app.appComponent) {
            DaggerChatComponent.factory().create(
                androidDependencies = this,
                domainDependencies = this,
                repositoryDependencies = this
            ).inject(this@ChatFragment)
        }

        chatStore = chatStoreFactory.create(
            chatActorFactory.create(
                chatRepositoryFactory.create(
                    streamId,
                    topicName,
                    emojiRepository.originalEmojiList
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.setFragmentResultListener(
            EmojiBottomModalFragment.RESULT_REQUEST_KEY,
            this
        ) { _, bundle ->
            val emojiName = bundle.getString(EmojiBottomModalFragment.EMOJI_SELECTED_NAME_KEY)!!
            val emojiCode = bundle.getString(EmojiBottomModalFragment.EMOJI_SELECTED_CODE_KEY)!!
            val messageId = bundle.getLong(EmojiBottomModalFragment.MESSAGE_ID_KEY)

            store.accept(
                ChatEvent.Ui.ReactionSent(
                    messageId,
                    emojiName
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            messages.apply {
                layoutManager = ScrollLinearLayoutManager(context)
                adapter = MessageAdapter(
                    personalRepository.meId,
                    this@ChatFragment::onReactionPressed,
                    this@ChatFragment::onMessageLongClicked
                )
                addItemDecoration(DateOnChatDecorator(context))
            }

            chatTitle.apply {
                title = "#${streamName}"
                setNavigationOnClickListener {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }

            topicSubtitle.text = String.format("Topic: %s", topicName)

            inputMessage.apply {
                doOnTextChanged { text, _, _, _ ->
                    chatActionButton.isActivated = !text.isNullOrBlank()
                }
            }

            chatActionButton.setOnClickListener(::onSendClicked)

            if (store.currentState.messages == null) {
                store.accept(ChatEvent.Ui.Started)
            }
        }
    }

    override fun createStore(): Store<ChatEvent, ChatEffect, ChatState> = chatStore.provide()

    override val initEvent: ChatEvent
        get() = ChatEvent.Ui.Init

    override fun render(state: ChatState) {
        loading(state.isLoading)
        state.messages?.let(::setMessages)
    }

    override fun handleEffect(effect: ChatEffect): Unit {
        return when (effect) {
            is ChatEffect.MessageReceived -> onMessageReceived(effect.messages)
            is ChatEffect.ShowError -> handleError(effect.error)
        }
    }

    private fun handleError(error: Throwable) {
        Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show()
    }

    override fun loading(turnOn: Boolean) {
        with(binding) {
            progress.isVisible = turnOn
            chatActionButton.isVisible = !turnOn
            inputMessage.isVisible = !turnOn
            messages.isVisible = !turnOn
        }
    }

    private fun clearTextField() {
        binding.inputMessage.text = null
    }

    private fun showNotSupportedFeatureNotification(view: View) {
        Toast.makeText(
            view.context,
            getString(R.string.file_attaching_toast),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun setMessages(messages: List<Message>) {
        (binding.messages.adapter as MessageAdapter).submitList(messages)
    }

    private fun onSendClicked(view: View) {
        with(binding) {
            if (chatActionButton.isActivated) {
                onMessageSent(inputMessage)
                clearTextField()
            } else {
                showNotSupportedFeatureNotification(view)
            }
        }
    }

    private fun onMessageSent(inputMessage: EditText) {
        val textMessage = inputMessage.text.toString()

        val message = Message(
            Message.NOT_SENT_MESSAGE,
            personalRepository.me.toSender(),
            textMessage,
            Calendar.getInstance().timeInMillis / 1000
        )

        (binding.messages.adapter as MessageAdapter).sendMessage(message) {
            store.accept(
                ChatEvent.Ui.MessageSent(
                    textMessage
                )
            )
        }
    }

    private fun onMessageReceived(messages: List<Message>) {
        (binding.messages.adapter as MessageAdapter).submitSentMessage(messages)
    }

    private fun onMessageLongClicked(messageId: Long): Boolean {
        EmojiBottomModalFragment(messageId).show(
            childFragmentManager,
            EmojiBottomModalFragment.TAG
        )
        return true
    }

    private fun onReactionPressed(
        isReactionSelectedNow: Boolean,
        messageId: Long,
        emojiName: String
    ) {
        if (isReactionSelectedNow) {
            store.accept(
                ChatEvent.Ui.ReactionRemoved(
                    messageId,
                    emojiName
                )
            )
        } else {
            store.accept(
                ChatEvent.Ui.ReactionSent(
                    messageId,
                    emojiName
                )
            )
        }
    }
}