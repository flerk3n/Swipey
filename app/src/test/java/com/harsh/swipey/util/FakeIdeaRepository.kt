package com.harsh.swipey.util

import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.repository.IdeaRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * In-memory [IdeaRepository] for ViewModel tests. Delegates the saved cache to
 * [SavedIdeasStore] so existing assertions on the store keep working, and serves a
 * configurable deck. Set [failDeck] to simulate a network error.
 */
class FakeIdeaRepository(
    private val deck: List<IdeaModel> = emptyList(),
) : IdeaRepository {

    var failDeck: Boolean = false

    override val saved: StateFlow<List<IdeaModel>> = SavedIdeasStore.saved

    override suspend fun fetchDeck(limit: Int, niche: String?): List<IdeaModel> {
        if (failDeck) throw RuntimeException("network down")
        return deck
    }

    override suspend fun refreshSaved() {
        // no-op: tests drive the store directly
    }

    override suspend fun like(idea: IdeaModel) {
        SavedIdeasStore.add(idea)
    }

    override suspend fun save(idea: IdeaModel) {
        SavedIdeasStore.add(idea)
    }

    override suspend fun dismiss(idea: IdeaModel) {
        // no-op
    }

    override suspend fun unsave(id: String) {
        SavedIdeasStore.remove(id)
    }

    override suspend fun updateTags(ideaId: String, tags: List<String>) {
        SavedIdeasStore.updateTags(ideaId, tags)
    }

    override suspend fun expandEcho(ideaId: String): String =
        deck.firstOrNull { it.id == ideaId }?.description.orEmpty()
}
