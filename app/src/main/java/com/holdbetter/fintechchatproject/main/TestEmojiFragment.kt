package com.holdbetter.fintechchatproject.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.chat.EmojiDialogAdapter
import com.holdbetter.fintechchatproject.chat.TestEmojiDialogAdapter
import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.services.Mapper.toReactionList
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class TestEmojiFragment : Fragment(R.layout.emoji_bottom_dialog) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val emojiAdapter = TestEmojiDialogAdapter(null)
        var emojiList = view.findViewById<RecyclerView>(R.id.emoji_list).apply {
            layoutManager = GridLayoutManager(activity, 6)
            adapter = emojiAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        ServiceProvider.getApi()
            .getAllEmoji()
            .subscribeOn(Schedulers.io())
            .map { it.toReactionList() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = emojiAdapter::setEmojiList
            )
    }
}