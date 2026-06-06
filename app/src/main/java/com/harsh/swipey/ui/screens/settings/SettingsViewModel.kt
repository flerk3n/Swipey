package com.harsh.swipey.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.swipey.data.ServiceLocator
import com.harsh.swipey.data.SettingsPrefs
import com.harsh.swipey.data.repository.UserSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val pushNotifications: Boolean = true,
    val dailyReminder: Boolean = true,
    val reducedMotion: Boolean = false,
    val notionConnected: Boolean = false,
    val calendarConnected: Boolean = false,
)

/**
 * Settings ViewModel. Cache-first: shows [SettingsPrefs] immediately, then refreshes from the
 * backend. Every toggle updates the local cache + UI instantly and best-effort syncs to the
 * server (which is the source of truth across reinstalls).
 */
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = SettingsPrefs(app)
    private val profileRepository = ServiceLocator.profileRepository
    private val notionRepository = ServiceLocator.notionRepository

    private val _uiState = MutableStateFlow(prefs.toUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _toastMessages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastMessages: SharedFlow<String> = _toastMessages.asSharedFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    init {
        // Hydrate from the server, falling back silently to the local cache if offline.
        viewModelScope.launch {
            runCatching { profileRepository.getSettings() }.getOrNull()?.let { remote ->
                applyRemote(remote)
            }
        }
    }

    fun setPushNotifications(value: Boolean) = update { it.copy(pushNotifications = value) }
    fun setDailyReminder(value: Boolean) = update { it.copy(dailyReminder = value) }
    fun setReducedMotion(value: Boolean) = update { it.copy(reducedMotion = value) }
    fun setNotionConnected(value: Boolean) = update { it.copy(notionConnected = value) }
    fun setCalendarConnected(value: Boolean) = update { it.copy(calendarConnected = value) }

    private fun update(transform: (SettingsUiState) -> SettingsUiState) {
        val next = transform(_uiState.value)
        _uiState.value = next
        persistLocal(next)
        viewModelScope.launch { runCatching { profileRepository.updateSettings(next.toDomain()) } }
    }

    private fun applyRemote(remote: UserSettings) {
        val next = SettingsUiState(
            pushNotifications = remote.pushNotifications,
            dailyReminder = remote.dailyReminder,
            reducedMotion = remote.reducedMotion,
            notionConnected = remote.notionConnected,
            calendarConnected = remote.calendarConnected,
        )
        _uiState.value = next
        persistLocal(next)
    }

    private fun persistLocal(state: SettingsUiState) {
        prefs.pushNotifications = state.pushNotifications
        prefs.dailyReminder = state.dailyReminder
        prefs.reducedMotion = state.reducedMotion
        prefs.notionConnected = state.notionConnected
        prefs.calendarConnected = state.calendarConnected
    }

    /** Push all saved ideas to Notion. Shows a toast with the result. */
    fun exportAllToNotion() {
        if (_isExporting.value) return
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val results = notionRepository.pushAll()
                val ok = results.count { it.ok }
                val fail = results.size - ok
                val msg = when {
                    results.isEmpty() -> "No saved ideas to export."
                    fail == 0 -> "Exported $ok idea${if (ok == 1) "" else "s"} to Notion ✓"
                    else -> "Exported $ok, failed $fail. Check Notion."
                }
                _toastMessages.tryEmit(msg)
            } catch (e: Exception) {
                _toastMessages.tryEmit("Notion export failed: ${e.message}")
            } finally {
                _isExporting.value = false
            }
        }
    }
}

private fun SettingsPrefs.toUiState() = SettingsUiState(
    pushNotifications = pushNotifications,
    dailyReminder = dailyReminder,
    reducedMotion = reducedMotion,
    notionConnected = notionConnected,
    calendarConnected = calendarConnected,
)

private fun SettingsUiState.toDomain() = UserSettings(
    pushNotifications = pushNotifications,
    dailyReminder = dailyReminder,
    reducedMotion = reducedMotion,
    notionConnected = notionConnected,
    calendarConnected = calendarConnected,
)
