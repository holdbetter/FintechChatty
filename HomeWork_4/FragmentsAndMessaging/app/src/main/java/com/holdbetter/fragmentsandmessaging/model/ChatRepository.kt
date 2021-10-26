package com.holdbetter.fragmentsandmessaging.model

import com.holdbetter.fragmentsandmessaging.R
import com.holdbetter.fragmentsandmessaging.model.IChatRepository.UserId
import com.holdbetter.fragmentsandmessaging.model.IChatRepository.TopicId
import java.util.*

class ChatRepository : IChatRepository {
    override val users: TreeSet<StupidUser> = sortedSetOf(
        compareBy(StupidUser::id),
        StupidUser(UserId.VILEN_ID,
            "Vilen Evseev",
            "me@holdbetter.dev",
            R.drawable.hades,
            isOnline = true),
        StupidUser(UserId.ALEXEY_ID,
            "Alexey Korchagin",
            "alexey.korchagin@tinkoff.ru",
            R.drawable.alexey),
        StupidUser(UserId.ASAP_ROCKY,
            "A\$AP ROCKY",
            "asap.rocky@music.com",
            R.drawable.rocky,
            isOnline = true),
    )

    override val testingMessages = arrayListOf(
        Message(
            users.elementAt(UserId.VILEN_ID),
            "Так, ну че, тестим этот чат с крутыми пацанами",
            TopicId.TESTING_ID,
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            },
        ),
        Message(
            users.elementAt(UserId.ALEXEY_ID),
            "Ха-ха, внатуре, всем привет",
            TopicId.TESTING_ID,
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            }
        ),
        Message(
            users.elementAt(UserId.ASAP_ROCKY),
            "Who the fuck are you two?",
            TopicId.TESTING_ID,
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            },
            arrayListOf(
                Reaction(arrayListOf(0, 1), "U+1F602")
            )
        ),
        Message(
            users.elementAt(UserId.VILEN_ID),
            "АХВАХЫВХАЫВХАХ, FROM RUSSIA WITH LOVE, MEN ❤",
            TopicId.TESTING_ID,
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            },
        )
    )

    override val bruhMessages = arrayListOf(
        Message(
            users.elementAt(UserId.ALEXEY_ID),
            "Привет! Отличная новость, тебя повысили",
            TopicId.BRUH_ID,
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            },
        ),
        Message(
            users.elementAt(UserId.VILEN_ID),
            "Здорово. Раньше я мыл полы, а сейчас что?",
            TopicId.BRUH_ID,
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            },
            arrayListOf(
                Reaction(arrayListOf(0, 1), "U+1F601"),
                Reaction(arrayListOf(0, 1, 2), "U+1F602"),
                Reaction(arrayListOf(1), "U+1F603"),
                Reaction(arrayListOf(0), "U+1F604"),
            )
        ),
        Message(
            users.elementAt(UserId.ALEXEY_ID),
            "Пока не решили, но то что повысили это прям 100 процентов",
            TopicId.BRUH_ID,
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            },
            arrayListOf(
                Reaction(arrayListOf(1), "U+1F607"),
                Reaction(arrayListOf(0), "U+1F606"),
            )
        ),
        Message(
            users.elementAt(UserId.VILEN_ID),
            """Кажется, мой ментор умер и чтобы было не так грустно, я оставил анекдот
            |
            |Анекдот:
            |Холмс с Ватсоном отправились в поход. Ночью Холмс, проснувшись, будит Ватсона:
            |– Ватсон, посмотрите на небо и скажите, что вы видите! – требует он.
            |– Я вижу мириады звезд, Холмс! – восклицает Ватсон.
            |– И что же это означает?
            |Ватсон ненадолго задумывается:
            |– С астрономической точки зрения это означает, что во Вселенной существуют миллионы галактик и, вероятно, миллиарды планет. С точки зрения метеорологии завтра, скорее всего, будет прекрасный день. Ну, а если рассуждать, подобно теологам, мы можем прийти к выводу, что Бог всемогущ, а мы мелки и незначительны. А вы что думаете об этом, Холмс?
            |– Ватсон, вы идиот! У нас украли палатку!
        """.trimMargin(),
            TopicId.BRUH_ID,
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            }
        ),
    )

    override val topics = /*ArrayList<Topic>()*/ arrayListOf(
        Topic(TopicId.BRUH_ID, "Bruh", "#E9C46A", bruhMessages, 0),
        Topic(TopicId.TESTING_ID, "Testing", "#2A9D8F", testingMessages, 0),
    )

    override val hashtagStreams = arrayListOf(
        HashtagStream(0, "#general", topics),
        HashtagStream(1, "#Development", topics),
        HashtagStream(2, "#Design", topics),
        HashtagStream(3, "#PR", topics),
        HashtagStream(4, "#gaming", topics),
        HashtagStream(5, "#indie-pop", topics),
        HashtagStream(6, "#Habrahabr", topics),
    )
}