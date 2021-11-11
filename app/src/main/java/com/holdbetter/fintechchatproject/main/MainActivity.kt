package com.holdbetter.fintechchatproject.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.main.viewmodel.EmojiViewModel

class MainActivity : AppCompatActivity() {

    val viewModel: EmojiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progress = findViewById<CircularProgressIndicator>(R.id.progress)

        viewModel.isEmojiLoaded.observe(this, { isEmojiLoaded ->
            if (!isEmojiLoaded) {
                progress.isVisible = true
            } else {
                supportFragmentManager.beginTransaction()
                    .add(R.id.main_host_fragment, MainHostFragment.newInstance(R.id.channels))
                    .runOnCommit { progress.isVisible = false }
                    .commitAllowingStateLoss()
            }
        })

        if (savedInstanceState == null) {
            viewModel.getEmojiList()
        }
    }
}