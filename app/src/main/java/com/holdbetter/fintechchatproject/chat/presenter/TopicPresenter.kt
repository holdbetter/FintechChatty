package com.holdbetter.fintechchatproject.chat.presenter

import com.holdbetter.fintechchatproject.chat.view.ITopicViewer
import com.holdbetter.fintechchatproject.model.HashtagStream
import com.holdbetter.fintechchatproject.model.Message
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.model.Topic
import com.holdbetter.fintechchatproject.model.repository.IChatRepository
import com.holdbetter.fintechchatproject.services.Util
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class TopicPresenter(
    override val topicId: Int,
    private val chatRepository: IChatRepository,
    private val topicViewer: ITopicViewer,
) : ITopicPresenter {
    private val compositeDisposable = CompositeDisposable()

    private val currentTopic: Topic by lazy {
        Single.just(topicId)
            .subscribeOn(Schedulers.io())
            .delay(50, TimeUnit.MILLISECONDS)
            .map { chatRepository.topics.find { t -> t.id == topicId }!! }
            .blockingGet()
    }

    override fun getTopicById(id: Int): Single<Topic> {
        return Single.just(id)
            .subscribeOn(Schedulers.io())
            .delay(50, TimeUnit.MILLISECONDS)
            .map { chatRepository.topics.find { t -> t.id == id }!! }
    }

    private fun Topic.getMessageById(id: Int): Message {
        return Single.just(id)
            .subscribeOn(Schedulers.io())
            .delay(50, TimeUnit.MILLISECONDS)
            .map { messages.find { it.id == id }!! }
            .blockingGet()
    }

    private fun List<Reaction>.getReactionByCode(emojiCode: String): Reaction? {
        return Maybe.just(emojiCode)
            .subscribeOn(Schedulers.io())
            .delay(50, TimeUnit.MILLISECONDS)
            .flatMap {
                Maybe.create<Reaction> { it ->
                    val reaction = find { it.emojiCode == emojiCode }
                    if (reaction != null) {
                        it.onSuccess(reaction)
                    }
                    it.onComplete()
                }
            }
            .blockingGet()
    }

    override fun getStreamById(id: Int): Single<HashtagStream> {
        return Single.just(id)
            .subscribeOn(Schedulers.io())
            .map { chatRepository.hashtagStreams.find { it.id == id }!! }
    }

    private fun addUserToReaction(reaction: Reaction, messageId: Int): Single<Int> {
        return Single.just(reaction)
            .subscribeOn(Schedulers.io())
            .map {
                it.usersId.add(Util.currentUserId)
                messageId
            }
    }

    private fun addNewReaction(
        messageId: Int,
        emojiCode: String,
        reactions: ArrayList<Reaction>,
    ): Single<Int> {
        return Single.just(messageId)
            .subscribeOn(Schedulers.io())
            .map {
                reactions.add(Reaction(arrayListOf(Util.currentUserId), emojiCode))
                messageId
            }
    }

    override fun addReactionUsingDialog(messageId: Int, emojiCode: String) {
        Maybe.create<Int> { emitter ->
            val reactions = currentTopic.getMessageById(messageId).reactions
            val reaction = reactions.getReactionByCode(emojiCode)

            val messageIdToUpdate = if (reaction != null) {
                isUserInReaction(reaction)
                    .filter { !it }
                    .flatMapSingle {
                        addUserToReaction(reaction, messageId)
                    }.blockingGet()
            } else {
                addNewReaction(messageId, emojiCode, reactions).blockingGet()
            }

            if (messageIdToUpdate != null) {
                emitter.onSuccess(messageIdToUpdate)
            }
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = topicViewer::onReactionUpdated
            ).addTo(compositeDisposable)
    }

    private fun isUserInReaction(reaction: Reaction): Single<Boolean> {
        return Single.create<Boolean> {
            it.onSuccess(reaction.usersId.contains(Util.currentUserId))
        }.subscribeOn(Schedulers.io())
    }

    private fun removeUserFromReaction(
        reaction: Reaction,
        reactions: ArrayList<Reaction>,
        messageId: Int,
    ): Single<Int> {
        return Single.just(messageId)
            .subscribeOn(Schedulers.io())
            .map {
                reaction.usersId.remove(Util.currentUserId)
                it
            }.doOnSuccess {
                if (reaction.usersId.size == 0) {
                    deleteReaction(reaction, reactions)
                }
            }
    }

    override fun updateReaction(messageId: Int, emojiCode: String) {
        Single.create<Int> { emitter ->
            val reactions = currentTopic.getMessageById(messageId).reactions
            val reaction = reactions.getReactionByCode(emojiCode)!!

            val messageIdToUpdate = isUserInReaction(reaction)
                .flatMap { userInReaction ->
                    if (userInReaction) {
                        removeUserFromReaction(reaction, reactions, messageId)
                    } else {
                        addUserToReaction(reaction, messageId)
                    }
                }.blockingGet()

            emitter.onSuccess(messageIdToUpdate)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = topicViewer::onReactionUpdated
            ).addTo(compositeDisposable)
    }

    private fun deleteReaction(reaction: Reaction, reactions: ArrayList<Reaction>) {
        Single.just(reaction)
            .subscribeOn(Schedulers.io())
            .map { reactions.remove(reaction) }
            .subscribe()
            .addTo(compositeDisposable)
    }

    override fun addMessage(messageText: String) {
        Single.just(messageText)
            .subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .map {
                currentTopic.messages.add(
                    Message(
                        chatRepository.currentUser,
                        messageText,
                        topicId
                    )
                )
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    topicViewer.onMessageInserted(currentTopic.messages.lastIndex)
                }
            )
    }

    override fun bind() {
        topicViewer.startLoading()
        Single.create<Topic> { emitter ->
            emitter.onSuccess(currentTopic)
        }
            .subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { topic ->
                topicViewer.setMessages(topic.messages)
                topicViewer.setTopicName("Topic: ${topic.name}")
            }
            .observeOn(Schedulers.io())
            .flatMap { getStreamById(it.hashtagId) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { stream ->
                    topicViewer.setHashtagTitle(stream.name)
                    topicViewer.stopLoading()
                }
            ).addTo(compositeDisposable)
    }

    override fun unbind() {
        compositeDisposable.clear()
    }
}