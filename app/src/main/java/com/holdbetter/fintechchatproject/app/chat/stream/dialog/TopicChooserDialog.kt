package com.holdbetter.fintechchatproject.app.chat.stream.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.transition.MaterialContainerTransform
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.app.chat.stream.dialog.di.DaggerTopicDialogComponent
import com.holdbetter.fintechchatproject.app.chat.stream.dialog.di.TopicDialogComponent
import com.holdbetter.fintechchatproject.app.chat.stream.dialog.elm.TopicChooserEffect
import com.holdbetter.fintechchatproject.app.chat.stream.dialog.elm.TopicChooserEvent
import com.holdbetter.fintechchatproject.app.chat.stream.dialog.elm.TopicChooserState
import com.holdbetter.fintechchatproject.app.chat.stream.dialog.elm.TopicChooserStore
import com.holdbetter.fintechchatproject.databinding.FragmentTopicChooserBinding
import com.holdbetter.fintechchatproject.model.TopicChooser
import com.holdbetter.fintechchatproject.room.services.DatabaseMapper.toTopicChooser
import com.holdbetter.fintechchatproject.services.FragmentExtensions.app
import com.holdbetter.fintechchatproject.ui.MarginItemDecoration
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class TopicChooserDialog :
    ElmFragment<TopicChooserEvent, TopicChooserEffect, TopicChooserState>(R.layout.fragment_topic_chooser) {
    companion object {
        const val TAG = "TopicChooserDialog"
        const val STREAM_ID_KEY = "streamId"
        const val SELECTED_TOPIC_KEY = "topicName"
        const val RESULT_REQUEST_KEY = "selectedTopic"

        fun newInstance(streamId: Long): TopicChooserDialog {
            return TopicChooserDialog().apply {
                arguments = bundleOf(
                    STREAM_ID_KEY to streamId
                )
            }
        }
    }

    private lateinit var topicDialogComponent: TopicDialogComponent

    @Inject
    lateinit var topicChooserStore: TopicChooserStore

    val binding by viewBinding(FragmentTopicChooserBinding::bind)

    var streamId: Long by notNull()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        streamId = requireArguments().getLong(STREAM_ID_KEY)

        topicDialogComponent = DaggerTopicDialogComponent.factory().create(
            androidDependencies = app.appComponent
        )
        topicDialogComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            topics.apply {
                layoutManager =
                    LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(MarginItemDecoration(20f))
                adapter = TopicChooserAdapter(::onTopicSelected)
            }

            dialogSuccess.apply {
                clipToOutline = true
                setOnClickListener {
                    val adapter = binding.topics.adapter as TopicChooserAdapter
                    val resultTopicName = adapter.getSelected()?.name ?: createTopic.text.toString()
                    onDialogFinishing(resultTopicName)
                }
            }
        }

        if (store.currentState.topics == null) {
            store.accept(TopicChooserEvent.Ui.Started(streamId))
        }
    }

    private fun onTopicSelected(topic: TopicChooser) {
        val state = !topic.isSelected

        val adapter = binding.topics.adapter as TopicChooserAdapter
        adapter.clearSelections()

        topic.isSelected = state
        adapter.notifyDataSetChanged()
    }

    override val initEvent: TopicChooserEvent
        get() = TopicChooserEvent.Ui.Init

    override fun createStore(): Store<TopicChooserEvent, TopicChooserEffect, TopicChooserState> =
        topicChooserStore.provide()

    override fun render(state: TopicChooserState) {

    }

    override fun mapList(state: TopicChooserState): List<Any> {
        state.topics?.let {
            return it.map { topicEntity -> topicEntity.toTopicChooser() }
        } ?: return super.mapList(state)
    }

    override fun renderList(state: TopicChooserState, list: List<Any>) {
        state.topics?.let {
            val adapter = binding.topics.adapter as TopicChooserAdapter
            adapter.setTopics(list as List<TopicChooser>)
        }
    }

    private fun onDialogFinishing(chosenTopicName: String) {
        if (chosenTopicName.isNotBlank()) {
            setFragmentResult(
                RESULT_REQUEST_KEY, bundleOf(
                    SELECTED_TOPIC_KEY to chosenTopicName
                )
            )
        }

        requireActivity().supportFragmentManager.popBackStack()
    }
}