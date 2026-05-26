package com.aetheria.bigtype

import androidx.multidex.MultiDexApplication
import android.util.Log
import com.aetheria.bigtype.llm.LLMClient
import com.aetheria.bigtype.bridge.BridgeClient
import com.aetheria.bigtype.keyboard.ModifierStateManager
import com.aetheria.bigtype.privacy.PrivacyDetector
import com.aetheria.bigtype.clipboard.BigTypeDatabase
import androidx.room.Room

class BigTypeApp : MultiDexApplication() {

    companion object {
        lateinit var llmClient: LLMClient
        lateinit var bridgeClient: BridgeClient
        lateinit var modifierStateManager: ModifierStateManager
        lateinit var privacyDetector: PrivacyDetector
        lateinit var database: BigTypeDatabase
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BigType", "Application.onCreate() called successfully")

        // Manual dependency injection
        llmClient = LLMClient()
        bridgeClient = BridgeClient()
        modifierStateManager = ModifierStateManager()
        privacyDetector = PrivacyDetector()
        database = Room.databaseBuilder(
            this,
            BigTypeDatabase::class.java,
            "bigtype.db"
        ).fallbackToDestructiveMigration().build()

        Log.d("BigType", "All dependencies initialized")
    }
}
