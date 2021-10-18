package com.holdbetter.chatonrecycler.services

import android.content.Context
import android.util.TypedValue
import com.holdbetter.chatonrecycler.R
import com.holdbetter.chatonrecycler.model.Message
import com.holdbetter.chatonrecycler.model.User
import java.util.*
import kotlin.collections.HashSet

object Util { // aka pomoika
    val calendarInstance: Calendar = Calendar.getInstance()
    var currentUserId = 0

    fun Context.dpToPx(dp: Float): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        )
        return px.toInt()
    }

    fun Context.spToPx(sp: Float): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            this.resources.displayMetrics
        )
        return px.toInt()
    }

    val emojiUnicode = arrayOf(
        "U+1F911", "U+1F975", "U+1F976",
        "U+1F973", "U+1F92C", "U+1F4A9"
    )

    val emojiHex = arrayOf(
        0x1F911, 0x1F975, 0x1F976,
        0x1F973, 0x1F92C, 0x1F4A9
    )

    val users: TreeSet<User> = TreeSet(compareBy(User::userId))

    val defaultMessages = arrayListOf(
        Message(
            User(1, "Alexey Korchagin", R.drawable.alexey),
            "Привет! Отличная новость, тебя повысили",
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            }
        ),
        Message(
            User(0, "Vilen Evseev", R.drawable.hades),
            "Здорово. Раньше я мыл полы, а сейчас что?",
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            }
        ),
        Message(
            User(1, "Alexey Korchagin", R.drawable.alexey),
            "Пока не решили, но то что повысили это прям 100 процентов",
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            }
        ),
        Message(
            User(0, "Vilen Evseev", R.drawable.hades),
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
            Calendar.getInstance().run {
                set(Calendar.MONTH, 4)
                timeInMillis
            }
        ),
    )
}