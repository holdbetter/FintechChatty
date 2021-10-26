package com.holdbetter.fragmentsandmessaging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.holdbetter.fragmentsandmessaging.fragment.MainHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_host_fragment, MainHostFragment.newInstance(R.id.channels))
                .commitAllowingStateLoss()
        }
    }
}