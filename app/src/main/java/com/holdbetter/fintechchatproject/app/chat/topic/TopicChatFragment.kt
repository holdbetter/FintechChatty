package com.holdbetter.fintechchatproject.app.chat.topic

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.chat.BaseChatFragment
import com.holdbetter.fintechchatproject.app.chat.di.DaggerChatComponent
import com.holdbetter.fintechchatproject.app.chat.elm.*
import com.holdbetter.fintechchatproject.app.chat.elm.topic.TopicChatActorFactory
import com.holdbetter.fintechchatproject.app.chat.elm.topic.TopicChatStore
import com.holdbetter.fintechchatproject.app.chat.elm.topic.TopicChatStoreFactory
import com.holdbetter.fintechchatproject.app.chat.services.DateOnChatDecorator
import com.holdbetter.fintechchatproject.app.chat.services.ReverseLayoutManager
import com.holdbetter.fintechchatproject.app.chat.view.BaseMessageAdapter
import com.holdbetter.fintechchatproject.app.chat.view.ChatOnScrollListener
import com.holdbetter.fintechchatproject.app.chat.view.topic.TopicMessageAdapter
import com.holdbetter.fintechchatproject.databinding.FragmentTopicChatBinding
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.domain.repository.TopicChatRepositoryFactory
import com.holdbetter.fintechchatproject.domain.services.NetworkMapper.toSender
import com.holdbetter.fintechchatproject.model.MessageItem
import com.holdbetter.fintechchatproject.room.services.UnexpectedRoomException
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import com.holdbetter.fintechchatproject.services.FragmentExtensions.createStyledSnackbar
import vivid.money.elmslie.core.store.Store
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class TopicChatFragment : BaseChatFragment(R.layout.fragment_topic_chat) {
    companion object {
        const val TOPIC_NAME_KEY = "topic"
        const val STREAM_ID_KEY = "streamId"
        const val STREAM_NAME_KEY = "streamName"

        fun newInstance(streamId: Long, streamName: String, topicName: String): TopicChatFragment {
            return TopicChatFragment().apply {
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
    lateinit var chatRepositoryFactory: TopicChatRepositoryFactory

    @Inject
    lateinit var chatActorFactory: TopicChatActorFactory

    @Inject
    lateinit var chatStoreFactory: TopicChatStoreFactory

    private lateinit var topicChatStore: TopicChatStore

    private val binding by viewBinding(FragmentTopicChatBinding::bind)

    var streamId: Long by notNull()
    lateinit var topicName: String
    lateinit var streamName: String

    private val connectionStateText
        get() = if (mainActivity.isNetworkAvailable) {
            resources.getString(R.string.online)
        } else {
            resources.getString(R.string.offline)
        }

    private val isConnectionAvailable: Boolean
        get() = mainActivity.isNetworkAvailable

    override val messages: List<MessageItem.Message>
        get() = store.currentState.messages!!

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
            ).inject(this@TopicChatFragment)
        }

        topicChatStore = chatStoreFactory.create(
            chatActorFactory.create(
                chatRepositoryFactory.create(
                    streamId,
                    emojiRepository.originalEmojiList,
                    personalRepository.meId,
                    topicName,
                )
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            messages.apply {
                layoutManager = ReverseLayoutManager(context)
                adapter = TopicMessageAdapter(
                    personalRepository.meId,
                    this@TopicChatFragment::onReactionPressed,
                    this@TopicChatFragment::onMessageLongClicked
                )
                addItemDecoration(DateOnChatDecorator(context))
            }

            chatTitle.apply {
                title = "#${streamName}"
                setNavigationOnClickListener {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }

            topicSubtitle.text = String.format(getString(R.string.topic_format), topicName)

            inputMessage.apply {
                doOnTextChanged { text, _, _, _ ->
                    chatActionButton.isActivated = !text.isNullOrBlank()
                }
            }

            chatActionButton.setOnClickListener(::onSendClicked)

            update.setOnClickListener { store.accept(ChatEvent.Ui.Retry) }

            if (store.currentState.messages == null) {
                store.accept(ChatEvent.Ui.Started)
            }
        }

        mainActivity.addNetworkCallback(this::onNetworkStateChanged)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.removeNetworkCallback(this::onNetworkStateChanged)
    }

    override fun createStore(): Store<ChatEvent, ChatEffect, ChatState> = topicChatStore.provide()

    override val initEvent: ChatEvent
        get() = ChatEvent.Ui.Init

    override fun render(state: ChatState) {
        loading(state.isLoading)

        state.messages?.let {
            binding.emptyData.isVisible = it.isEmpty() && state.isCachedData!!
            setMessages(it, state.isLastPortion!!, state.isCachedData!!)
        }

        state.isCachedData?.let {
            cacheShowing(state.isCachedData)
        }
    }

    override fun loading(turnOn: Boolean) {
        with(binding) {
            if (turnOn) {
                progress.isVisible = true
                messages.isVisible = false
                connectionState.isVisible = false
                update.isVisible = false
                chatActionButton.isVisible = false
                inputMessage.isVisible = false
                emptyData.isVisible = false
            } else {
                progress.isVisible = false
                messages.isVisible = true
            }
        }
    }

    override fun setMessages(
        messages: List<MessageItem>,
        isLastPortion: Boolean,
        isShowingCachedData: Boolean
    ) {
        with(binding.messages) {
            isVisible = messages.isNotEmpty()

            val adapter = (adapter as BaseMessageAdapter)
            if (!isLastPortion && !isShowingCachedData) {
                val messagesWithHeader =
                    listOf(
                        MessageItem.HeaderLoading(),
                        *messages.toTypedArray()
                    )
                adapter.submitList(messagesWithHeader) {
                    addOnScrollListener(ChatOnScrollListener(::onChatEdgeReaching))
                }
            } else {
                adapter.submitList(messages)
            }
        }
    }

    private fun cacheShowing(isCache: Boolean) {
        with(binding) {
            connectionState.isVisible = isCache
            if (isCache) {
                connectionState.isEnabled = isConnectionAvailable
                connectionState.text = connectionStateText
            }

            update.isVisible = isCache
            chatActionButton.isVisible = !isCache
            inputMessage.isVisible = !isCache
        }
    }

    override fun handleEffect(effect: ChatEffect): Unit {
        return when (effect) {
            is ChatEffect.CacheError -> handleDatabaseError(effect.error)
            is ChatEffect.ReactionError -> handleReactionError(effect.error)
        }
    }

    private fun handleReactionError(error: Throwable) {
        val snackbar = createStyledSnackbar()
        when (error) {
            is NotConnectedException, is IOException -> {
                snackbar.setText(R.string.no_connection)
                snackbar.duration = Snackbar.LENGTH_SHORT
                snackbar.show()
            }
        }
    }

    override fun onSendClicked(view: View) {
        with(binding) {
            if (chatActionButton.isActivated) {
                onMessageSent(inputMessage)
                clearTextField()
            } else {
                showNotSupportedFeatureNotification()
            }
        }
    }

    override fun onMessageSent(inputMessage: EditText) {
        val textMessage = inputMessage.text.toString()

        val message = MessageItem.Message(
            MessageItem.Message.NOT_SENT_MESSAGE,
            personalRepository.me.toSender(),
            textMessage,
            topicName,
            Calendar.getInstance().timeInMillis / 1000
        )

        with(binding.messages) {
            val messageAdapter = (adapter as BaseMessageAdapter)

            messageAdapter.sendMessage(message) {
                scrollToPosition(messageAdapter.itemCount - 1)
                store.accept(
                    ChatEvent.Ui.MessageSent(
                        textMessage,
                        topicName
                    )
                )
            }
        }
    }

    override fun onNetworkStateChanged(isAvailable: Boolean) {
        store.currentState.isCachedData?.let { isCachedData ->
            if (isCachedData) {
                with(binding) {
                    connectionState.isEnabled = isAvailable
                    connectionState.text = if (isAvailable) {
                        resources.getString(R.string.online)
                    } else {
                        resources.getString(R.string.offline)
                    }
                }
            }
        }
    }

    private fun handleDatabaseError(error: Throwable) {
        when (error) {
            is UnexpectedRoomException -> {
                createStyledSnackbar(R.string.unexpected_room_exception).show()
            }
        }
    }

    private fun clearTextField() {
        binding.inputMessage.text = null
    }

    private fun showNotSupportedFeatureNotification() {
        createStyledSnackbar(R.string.file_attaching_toast).show()
    }
}