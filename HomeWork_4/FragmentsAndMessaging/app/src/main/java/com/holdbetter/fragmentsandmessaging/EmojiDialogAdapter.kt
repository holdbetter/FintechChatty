package com.holdbetter.fragmentsandmessaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holdbetter.fragmentsandmessaging.components.FlexBoxLayout
import com.holdbetter.fragmentsandmessaging.components.ReactionView
import com.holdbetter.fragmentsandmessaging.model.Reaction
import com.holdbetter.fragmentsandmessaging.services.Util
import java.lang.ref.WeakReference

class EmojiDialogAdapter(
    val viewBoxDelivery: WeakReference<View>,
    val emojiBottomModalFragment: EmojiBottomModalFragment,
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
                if (emojiBottomModalFragment.showsDialog) {
                    emojiBottomModalFragment.dismiss()
                    if (viewBoxDelivery.get()!!.id != R.id.flexbox) {
                        this@EmojiDialogAdapter.viewBoxDelivery.get()!!
                            .findViewById<FlexBoxLayout>(R.id.flexbox)
                            .let { flexbox ->
                                flexbox.addView(
                                    ReactionView(Reaction(arrayListOf(), "") , viewBoxDelivery.get()!!.context).apply {
                                        emojiUnicode =
                                            emoji.text.toString().codePointAt(0).toString()
                                        count = 1
                                        isSelected = true
                                        layoutParams = FrameLayout.LayoutParams(
                                            FrameLayout.LayoutParams.WRAP_CONTENT,
                                            FrameLayout.LayoutParams.WRAP_CONTENT
                                        )
                                    }, flexbox.childCount - 1
                                )
                            }
                    } else {
                        val flexbox = viewBoxDelivery.get()!! as FlexBoxLayout
                        flexbox.addView(
                            ReactionView(Reaction(arrayListOf(), ""), viewBoxDelivery.get()!!.context).apply {
                                emojiUnicode = emoji.text.toString().codePointAt(0).toString()
                                count = 1
                                isSelected = true
                                layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT
                                )
                            }, flexbox.childCount - 1
                        )
                    }
                }
            }
        }
    }
}