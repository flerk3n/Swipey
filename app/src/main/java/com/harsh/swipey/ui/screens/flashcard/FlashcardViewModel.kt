package com.harsh.swipey.ui.screens.flashcard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.harsh.swipey.data.NicheMapping
import com.harsh.swipey.data.OnboardingPrefs
import com.harsh.swipey.data.ServiceLocator
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.repository.IdeaRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for the Flashcard screen. */
sealed interface FlashcardUiState {
    data object Loading : FlashcardUiState
    data class Content(val cards: List<IdeaModel>) : FlashcardUiState
    data object Empty : FlashcardUiState
    data class Error(val message: String) : FlashcardUiState
}

/**
 * Owns the swipe deck and routes swipe/button actions to the [IdeaRepository], which talks
 * to the backend and keeps the shared saved-ideas cache in sync. Swipes are applied to the
 * local queue immediately (optimistic) and persisted in the background.
 */
class FlashcardViewModel(
    private val repository: IdeaRepository,
    private val niche: String? = null,
) : ViewModel() {

    private val queue = ArrayDeque<IdeaModel>()

    private val _uiState = MutableStateFlow<FlashcardUiState>(FlashcardUiState.Loading)
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    private val _toastMessages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastMessages: SharedFlow<String> = _toastMessages.asSharedFlow()

    init {
        loadDeck()
    }

    private fun loadDeck() {
        viewModelScope.launch {
            _uiState.value = FlashcardUiState.Loading
            // Keep the saved cache fresh so the streak count is accurate.
            runCatching { repository.refreshSaved() }
            try {
                val deck = repository.fetchDeck(niche = niche)
                queue.clear()
                queue.addAll(deck)
                publish()
            } catch (e: Exception) {
                _uiState.value = FlashcardUiState.Error(
                    "Couldn't reach the server. Check that the backend is running.",
                )
            }
        }
    }

    fun onSwiped(idea: IdeaModel, direction: SwipeDirection) {
        when (direction) {
            SwipeDirection.Left -> dismiss(idea)
            SwipeDirection.Right -> like(idea)
            SwipeDirection.Up -> save(idea)
        }
    }

    fun dismissTop() = topCard()?.let(::dismiss)
    fun likeTop() = topCard()?.let(::like)
    fun saveTop() = topCard()?.let(::save)

    fun reset() = loadDeck()

    private fun topCard(): IdeaModel? = queue.firstOrNull()

    private fun dismiss(idea: IdeaModel) {
        removeFromQueue(idea)
        publish()
        viewModelScope.launch {
            runCatching { repository.dismiss(idea) }
                .onFailure { _toastMessages.tryEmit("Offline — swipe not saved") }
        }
    }

    private fun like(idea: IdeaModel) {
        removeFromQueue(idea)
        _toastMessages.tryEmit("Liked — added to your ideas")
        publish()
        viewModelScope.launch {
            runCatching { repository.like(idea) }
                .onFailure { _toastMessages.tryEmit("Offline — couldn't save to server") }
        }
    }

    private fun save(idea: IdeaModel) {
        removeFromQueue(idea)
        _toastMessages.tryEmit("Saved to your ideas")
        publish()
        viewModelScope.launch {
            runCatching { repository.save(idea) }
                .onFailure { _toastMessages.tryEmit("Offline — couldn't save to server") }
        }
    }

    private fun removeFromQueue(idea: IdeaModel) {
        queue.removeAll { it.id == idea.id }
    }

    private fun publish() {
        _uiState.value =
            if (queue.isEmpty()) FlashcardUiState.Empty
            else FlashcardUiState.Content(queue.toList())
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                val nicheId = OnboardingPrefs(app).nicheId
                FlashcardViewModel(
                    repository = ServiceLocator.ideaRepository,
                    niche = NicheMapping.backendNiche(nicheId),
                )
            }
        }
    }
}
