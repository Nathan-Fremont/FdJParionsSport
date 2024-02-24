package com.nathanfremont.fdjparionssport

import android.app.Application
import com.nathanfremont.common.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.IS_LOGGABLE) {
            Timber.plant(CustomDebugTree())
        } else {
            Timber.plant(NoOpTree())
        }
    }
}