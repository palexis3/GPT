package com.example.gpt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant

@HiltAndroidApp
class GPTApp: Application() {
    override fun onCreate() {
        super.onCreate()
        plant(Timber.DebugTree())
    }
}
