package com.nikhil.here.message_to_action

import android.app.Application
import com.mocklets.pluto.Pluto
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initializePluto()
    }

    private fun initializePluto() {
        Pluto.initialize(this)
    }
}