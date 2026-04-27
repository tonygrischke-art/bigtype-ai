package com.aetheria.bigtype.keyboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppProfileManager @Inject constructor() {
    private val profiles = mutableMapOf<String, AppProfile>()
    private val _currentProfile = MutableStateFlow<AppProfile?>(null)
    val currentProfile: StateFlow<AppProfile?> = _currentProfile

    fun loadProfile(packageName: String) {
        _currentProfile.value = profiles[packageName]
    }

    fun saveProfile(profile: AppProfile) {
        profiles[profile.packageName] = profile
    }

    fun getVibeForApp(packageName: String): VibeMode {
        return profiles[packageName]?.settings?.vibe ?: VibeMode.CASUAL
    }
}

data class AppProfile(
    val packageName: String,
    val settings: KeyboardSettings
)

data class KeyboardSettings(
    val isDevMode: Boolean = false,
    val vibe: VibeMode = VibeMode.CASUAL,
    val isAutocorrectEnabled: Boolean = true
)