package com.harsh.swipey.data.repository

import com.harsh.swipey.data.remote.ProfileDto
import com.harsh.swipey.data.remote.ProfileUpdateBody
import com.harsh.swipey.data.remote.SettingsDto
import com.harsh.swipey.data.remote.SwipeyApi

/** App-side profile model (server is the source of truth; prefs are a local cache). */
data class UserProfile(
    val displayName: String = "Editor",
    val nicheId: String? = null,
    val goalId: String? = null,
    val onboarded: Boolean = false,
    val settings: UserSettings = UserSettings(),
)

data class UserSettings(
    val pushNotifications: Boolean = true,
    val dailyReminder: Boolean = true,
    val reducedMotion: Boolean = false,
    val notionConnected: Boolean = false,
    val calendarConnected: Boolean = false,
)

/** Profile + settings sync against the backend (single demo user). */
interface ProfileRepository {
    suspend fun getProfile(): UserProfile
    suspend fun completeOnboarding(nicheId: String?, goalId: String?)
    suspend fun getSettings(): UserSettings
    suspend fun updateSettings(settings: UserSettings)
}

class RemoteProfileRepository(
    private val api: SwipeyApi,
) : ProfileRepository {

    override suspend fun getProfile(): UserProfile = api.getProfile().toDomain()

    override suspend fun completeOnboarding(nicheId: String?, goalId: String?) {
        api.updateProfile(
            ProfileUpdateBody(nicheId = nicheId, goalId = goalId, onboarded = true),
        )
    }

    override suspend fun getSettings(): UserSettings = api.getSettings().toDomain()

    override suspend fun updateSettings(settings: UserSettings) {
        api.updateSettings(settings.toDto())
    }
}

// --- Mappers -----------------------------------------------------------------

private fun ProfileDto.toDomain() = UserProfile(
    displayName = displayName,
    nicheId = nicheId,
    goalId = goalId,
    onboarded = onboarded,
    settings = settings.toDomain(),
)

private fun SettingsDto.toDomain() = UserSettings(
    pushNotifications = pushNotifications,
    dailyReminder = dailyReminder,
    reducedMotion = reducedMotion,
    notionConnected = notionConnected,
    calendarConnected = calendarConnected,
)

private fun UserSettings.toDto() = SettingsDto(
    pushNotifications = pushNotifications,
    dailyReminder = dailyReminder,
    reducedMotion = reducedMotion,
    notionConnected = notionConnected,
    calendarConnected = calendarConnected,
)
