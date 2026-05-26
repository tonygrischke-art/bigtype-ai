package com.aetheria.bigtype

import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import android.util.Log

@HiltAndroidApp
class BigTypeApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Log.d("BigType", "Application.onCreate() called successfully")
    }
}
