package com.aetheria.bigtype

import android.app.Application
import android.util.Log

class BigTypeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("BigType", "Application.onCreate() called successfully")
    }
}
