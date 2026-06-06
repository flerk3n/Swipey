package com.harsh.swipey.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.swipey.data.OnboardingPrefs
import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.ServiceLocator
import com.harsh.swipey.data.model.OnboardingData
import com.harsh.swipey.data.repository.UserProfile
import com.harsh.swipey.ui.screens.dashboard.nicheDisplayName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Profile state. Reads niche/goal/name from the backend (source of truth), falling back to
 * the local onboarding prefs when offline. Saved count comes from the shared cache.
 */
class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = OnboardingPrefs(app)
    private val profileRepository = ServiceLocator.profileRepository

    private val serverProfile = MutableStateFlow<UserProfile?>(null)

    val uiState: StateFlow<ProfileUiState> =
        combine(SavedIdeasStore.saved, serverProfile) { saved, server ->
            toState(saved.size, server)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = toState(SavedIdeasStore.saved.value.size, null),
        )

    init {
        viewModelScope.launch {
            runCatching { profileRepository.getProfile() }.getOrNull()?.let {
                serverProfile.value = it
            }
        }
    }

    private fun toState(savedCount: Int, server: UserProfile?): ProfileUiState {
        val nicheId = server?.nicheId ?: prefs.nicheId
        val goalId = server?.goalId ?: prefs.goalId
        return ProfileUiState(
            name = nicheDisplayName(nicheId),
            nicheName = OnboardingData.nicheById(nicheId)?.name,
            goalTitle = OnboardingData.goals.firstOrNull { it.id == goalId }?.title,
            savedCount = savedCount,
        )
    }
}
