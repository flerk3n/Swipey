package com.harsh.swipey.data

import com.harsh.swipey.data.model.IdeaModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Process-wide in-memory store of saved/liked ideas. Written to by `FlashcardViewModel`
 * and read by the Dashboard (Phase 7) so both stay in sync. No networking — replaced by a
 * real repository when the backend phase begins.
 */
object SavedIdeasStore {

    private val _saved = MutableStateFlow<List<IdeaModel>>(emptyList())
    val saved: StateFlow<List<IdeaModel>> = _saved.asStateFlow()

    /** Adds an idea if it isn't already saved (most-recent first). */
    fun add(idea: IdeaModel) {
        _saved.update { current ->
            if (current.any { it.id == idea.id }) current else listOf(idea) + current
        }
    }

    fun remove(id: String) {
        _saved.update { current -> current.filterNot { it.id == id } }
    }

    /** Replaces the entire cache (used to sync from the backend's saved list). */
    fun replaceAll(ideas: List<IdeaModel>) {
        _saved.value = ideas
    }

    /** Updates the tags of a cached idea (after a successful tag edit). */
    fun updateTags(id: String, tags: List<String>) {
        _saved.update { current ->
            current.map { if (it.id == id) it.copy(tags = tags) else it }
        }
    }

    fun isSaved(id: String): Boolean = _saved.value.any { it.id == id }

    /** Test/utility reset. */
    fun clear() {
        _saved.value = emptyList()
    }
}
