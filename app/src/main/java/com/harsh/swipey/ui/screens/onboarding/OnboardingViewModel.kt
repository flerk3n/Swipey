package com.harsh.swipey.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import com.harsh.swipey.data.model.Niche
import com.harsh.swipey.data.model.OnboardingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Selections gathered across the three onboarding steps. State only — no networking. */
data class OnboardingUiState(
    // Defaults to a seed-backed niche (Tech) so a demo can tap straight through; the user
    // can still switch to Gaming or Fashion (the other seeded niches).
    val selectedNicheId: String? = "tech",
    val selectedTopics: Set<String> = setOf("AI & Machine Learning"),
    val selectedGoalId: String? = "consistent",
) {
    val selectedNiche: Niche? get() = OnboardingData.nicheById(selectedNicheId)
    val canContinueNiche: Boolean get() = selectedNicheId != null
    val canContinueTopics: Boolean get() = selectedTopics.isNotEmpty()
    val canContinueGoal: Boolean get() = selectedGoalId != null
}

class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNicheSelected(nicheId: String) {
        _uiState.update { current ->
            // Changing niche clears previously chosen topics.
            if (current.selectedNicheId == nicheId) current
            else current.copy(selectedNicheId = nicheId, selectedTopics = emptySet())
        }
    }

    fun onTopicToggled(topic: String) {
        _uiState.update { current ->
            val topics = current.selectedTopics.toMutableSet()
            if (!topics.add(topic)) topics.remove(topic)
            current.copy(selectedTopics = topics)
        }
    }

    fun onGoalSelected(goalId: String) {
        _uiState.update { it.copy(selectedGoalId = goalId) }
    }
}
