package com.harsh.swipey.ui.screens.echo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.ServiceLocator
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.repository.IdeaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EchoUiState(
    val idea: IdeaModel?,
    /** Text visible on screen so far (typewriter reveal). */
    val displayedGenerated: String = "",
    val isGenerating: Boolean = false,
    val tags: List<String> = emptyList(),
    val notionPushed: Boolean = false,
    val isSendingToNotion: Boolean = false,
)

/**
 * ViewModel for the Expanded Echo screen. Receives [ideaId] via [SavedStateHandle]
 * (populated by the navigation back-stack argument).
 *
 * "Generate" calls the backend ([IdeaRepository.expandEcho], a Gemini-backed expansion) and
 * reveals the returned full text with a typewriter animation. If the call fails, it falls
 * back to revealing the idea's description so the screen still does something useful.
 */
class EchoViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: IdeaRepository,
) : ViewModel() {

    private val ideaId: String = checkNotNull(savedStateHandle["ideaId"])
    private val notionRepository = ServiceLocator.notionRepository

    private val _uiState = MutableStateFlow(resolveInitialState())
    val uiState: StateFlow<EchoUiState> = _uiState.asStateFlow()

    private fun resolveInitialState(): EchoUiState {
        // The idea was just liked/saved, so it lives in the shared cache.
        val idea = SavedIdeasStore.saved.value.firstOrNull { it.id == ideaId }
        return EchoUiState(idea = idea, tags = idea?.tags?.toMutableList() ?: emptyList())
    }

    /** Generates the expanded content via the backend, then types it out. */
    fun onGenerate() {
        if (_uiState.value.isGenerating) return
        _uiState.update { it.copy(displayedGenerated = "", isGenerating = true) }
        viewModelScope.launch {
            val full = runCatching { repository.expandEcho(ideaId) }
                .getOrNull()
                ?.takeIf { it.isNotBlank() }
                ?: _uiState.value.idea?.description.orEmpty()

            for (i in full.indices) {
                _uiState.update { it.copy(displayedGenerated = full.substring(0, i + 1)) }
                delay(10)
            }
            _uiState.update { it.copy(isGenerating = false) }
        }
    }

    /** Toggles a tag — adds if absent, removes if present. Persists to the backend. */
    fun onTagToggled(tag: String) {
        _uiState.update { state ->
            val updated = state.tags.toMutableList()
            if (!updated.remove(tag)) updated.add(tag)
            state.copy(tags = updated)
        }
        persistTags()
    }

    /** Adds a brand-new tag string. Trims and prepends '#' if missing. Persists. */
    fun onTagAdded(raw: String) {
        val tag = raw.trim().let { if (it.startsWith("#")) it else "#$it" }
        if (tag.length < 2) return
        _uiState.update { state ->
            if (tag in state.tags) state
            else state.copy(tags = state.tags + tag)
        }
        persistTags()
    }

    /** Best-effort push of the current tag list to the backend. */
    private fun persistTags() {
        val tags = _uiState.value.tags
        viewModelScope.launch { runCatching { repository.updateTags(ideaId, tags) } }
    }

    /** Push this idea to Notion. */
    fun onSendToNotion() {
        if (_uiState.value.isSendingToNotion) return
        _uiState.update { it.copy(isSendingToNotion = true) }
        viewModelScope.launch {
            runCatching { notionRepository.pushIdea(ideaId) }
                .onSuccess { _uiState.update { it.copy(notionPushed = true) } }
            _uiState.update { it.copy(isSendingToNotion = false) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                EchoViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    repository = ServiceLocator.ideaRepository,
                )
            }
        }
    }
}
