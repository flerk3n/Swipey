package com.harsh.swipey.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.harsh.swipey.data.ServiceLocator
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.repository.IdeaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

const val DAILY_TARGET = 5

/**
 * Dashboard state derived from the shared saved-ideas cache (kept in sync by the repository).
 * Greeting is time-of-day based; streak is a simple count for now (per the roadmap).
 */
data class DashboardUiState(
    val greeting: String = "Good day,",
    val userName: String = "Editor",
    val savedIdeas: List<IdeaModel> = emptyList(),
    val dailyTarget: Int = DAILY_TARGET,
    val streak: Int = 0,
) {
    val savedCount: Int get() = savedIdeas.size
    val progress: Float
        get() = savedCount.coerceAtMost(dailyTarget).toFloat() / dailyTarget
}

/** Reads saved ideas (kept in sync by the repository) and exposes derived dashboard state. */
class DashboardViewModel(
    private val repository: IdeaRepository,
) : ViewModel() {

    init {
        // Reconcile the cache with the server whenever the dashboard is shown.
        viewModelScope.launch { runCatching { repository.refreshSaved() } }
    }

    val uiState: StateFlow<DashboardUiState> = repository.saved
        .map(::toState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = toState(repository.saved.value),
        )

    private fun toState(saved: List<IdeaModel>) = DashboardUiState(
        greeting = greetingForHour(currentHour()),
        savedIdeas = saved,
        streak = saved.size,
    )

    private fun currentHour(): Int =
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    private fun greetingForHour(hour: Int): String = when (hour) {
        in 5..11 -> "Good morning,"
        in 12..16 -> "Good afternoon,"
        in 17..21 -> "Good evening,"
        else -> "Still up,"
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { DashboardViewModel(ServiceLocator.ideaRepository) }
        }
    }
}
