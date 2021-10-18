package com.holdbetter.chatonrecycler

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.holdbetter.chatonrecycler.components.ScrollLinearLayoutManager
import com.holdbetter.chatonrecycler.model.Message
import com.holdbetter.chatonrecycler.services.Util
import com.holdbetter.chatonrecycler.services.Util.calendarInstance
import com.holdbetter.chatonrecycler.services.Util.dpToPx
import com.holdbetter.chatonrecycler.services.Util.spToPx
import java.util.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val messageList = findViewById<RecyclerView>(R.id.messages).apply {
            layoutManager = ScrollLinearLayoutManager(this@ChatActivity)
            adapter = MessageAdapter(Util.defaultMessages)
            addItemDecoration(DateOnChatDecorator(this@ChatActivity))
        }

        val chatActionButton = findViewById<MaterialButton>(R.id.chat_action_button)

        val inputMessage = findViewById<EditText>(R.id.input_message).apply {
            doOnTextChanged { text, _, _, _ ->
                chatActionButton.isActivated = !text.isNullOrBlank()
            }
        }

        chatActionButton.setOnClickListener { sendTextButton ->
            if (sendTextButton.isActivated) {
                (messageList.adapter as MessageAdapter).addMessage(
                    Message(
                        Util.users.first(),
                        inputMessage.text.toString()
                    )
                )
                inputMessage.text = null
            } else {
                Toast.makeText(this@ChatActivity,
                    "Attaching files isn't ready yet",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}