package com.holdbetter.fintechchatproject.app.chat.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.model.Reaction
import com.holdbetter.fintechchatproject.services.Util

class EmojiDialogAdapter(
    val emojiList: List<Reaction>,
    val onEmojiSelectedAction: IOnEmojiSelectedListener,
) :
    RecyclerView.Adapter<EmojiDialogAdapter.EmojiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.emoji_in_dialog_instance, parent, false)
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojiList[position])
    }

    override fun getItemCount(): Int = emojiList.size

    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(reaction: Reaction) {
            emoji.text = Util.getEmojiByCode(reaction.emojiCode.toInt(16))

            emoji.setOnClickListener {
                onEmojiSelectedAction.finish(reaction.emojiName, reaction.emojiCode)
            }
        }

        private val emoji = itemView.findViewById<TextView>(R.id.emoji_text)
    }
}