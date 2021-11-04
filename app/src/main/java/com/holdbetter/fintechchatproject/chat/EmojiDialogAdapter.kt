package com.holdbetter.fintechchatproject.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.chat.view.IOnEmojiSelectedListener
import com.holdbetter.fintechchatproject.services.Util

class EmojiDialogAdapter(
    val onEmojiSelectedAction: IOnEmojiSelectedListener,
) :
    RecyclerView.Adapter<EmojiDialogAdapter.EmojiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.emoji_in_dialog_instance, parent, false)
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.emoji.text = Util.getEmojiByCode(Util.supportedEmojiList[position])
    }

    override fun getItemCount(): Int = Util.supportedEmojiList.size

    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emoji = itemView.findViewById<TextView>(R.id.emoji_text)!!

        init {
            emoji.setOnClickListener {
                onEmojiSelectedAction.finish(emoji.text.toString())
            }
        }
    }
}