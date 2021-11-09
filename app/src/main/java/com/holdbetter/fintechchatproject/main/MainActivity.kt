package com.holdbetter.fintechchatproject.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.holdbetter.fintechchatproject.R

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