package com.harsh.swipey.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.harsh.swipey.data.OnboardingPrefs
import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.ServiceLocator
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.repository.IdeaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

const val DAILY_TARGET = 10

/** Maps an onboarding niche id to a display persona name. Shared by Dashboard + Profile. */
fun nicheDisplayName(nicheId: String?): String = when (nicheId) {
    "tech", "tech_reviews" -> "Techie"
    "gaming" -> "Gamer"
    "fashion" -> "Fashionista"
    "finance" -> "Investor"
    "fitness" -> "Athlete"
    "food" -> "Foodie"
    "travel" -> "Explorer"
    "beauty" -> "Glam"
    "music" -> "Artist"
    "sports" -> "Sportsman"
    else -> "Creator"
}

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
    app: Application? = null,
) : ViewModel() {

    private val prefs: OnboardingPrefs? = app?.let { OnboardingPrefs(it) }

    init {
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
        userName = nicheDisplayName(prefs?.nicheId),
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
            initializer {
                val app = this[androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                DashboardViewModel(ServiceLocator.ideaRepository, app)
            }
        }
    }
}
