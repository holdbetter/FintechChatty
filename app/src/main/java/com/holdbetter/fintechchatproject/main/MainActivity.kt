package com.holdbetter.fintechchatproject.main

import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.main.view.EmojiLoadedState
import com.holdbetter.fintechchatproject.main.viewmodel.EmojiViewModel
import com.holdbetter.fintechchatproject.main.viewmodel.EmojiViewModelFactory
import com.holdbetter.fintechchatproject.navigation.channels.viewmodel.StreamViewModel
import com.holdbetter.fintechchatproject.navigation.channels.viewmodel.StreamViewModelFactory
import com.holdbetter.fintechchatproject.services.connectivity.ChatNetworkCallback
import com.holdbetter.fintechchatproject.services.connectivity.NetworkState
import com.holdbetter.fintechchatproject.services.connectivity.NetworkStateHolder
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val emojiViewModel: EmojiViewModel by viewModels {
        EmojiViewModelFactory((application as ChatApplication).connectivityManager)
    }

    private val streamViewModel: StreamViewModel by viewModels {
        val app = application as ChatApplication
        StreamViewModelFactory(app.streamRepository, app.connectivityManager)
    }

    private lateinit var state: NetworkState
    private lateinit var callback: ChatNetworkCallback

    lateinit var progress: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress = findViewById(R.id.progress)

        if (savedInstanceState == null) {
            emojiViewModel.getEmojiList()
        }

        emojiViewModel.isEmojiLoaded.observe(this, ::handleState)
    }

    private fun handleState(state: EmojiLoadedState?) {
        when (state) {
            is EmojiLoadedState.Error -> {
                progress.isVisible = false
                handleError(state.exception)
            }
            EmojiLoadedState.Loaded -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.main_host_fragment, MainHostFragment.newInstance(R.id.channels))
                    .runOnCommit { progress.isVisible = false }
                    .commitAllowingStateLoss()
            }
            EmojiLoadedState.Loading -> progress.isVisible = true
        }
    }

    override fun onResume() {
        super.onResume()

        state = NetworkState()
        callback = ChatNetworkCallback(state)
        NetworkStateHolder.bindState(state)
        getSystemService<ConnectivityManager>()?.registerDefaultNetworkCallback(callback)
    }

    override fun onPause() {
        super.onPause()

        getSystemService<ConnectivityManager>()?.unregisterNetworkCallback(callback)
    }

    private fun handleError(e: Throwable) {
        val appResource = resources
        val appTheme = theme

        val snackbar = Snackbar.make(progress , "Нет подключения к интернету", Snackbar.LENGTH_INDEFINITE).apply {
            setActionTextColor(appResource.getColor(R.color.blue_and_green, appTheme))
            setTextColor(appResource.getColor(android.R.color.black, appTheme))
            setBackgroundTint(appResource.getColor(R.color.white, appTheme))

            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).apply {
                typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
            }

            setAction("Повторить") { emojiViewModel.getEmojiList() }
        }

        when(e) {
            is NotConnectedException, is IOException -> snackbar.show()
        }
    }
}