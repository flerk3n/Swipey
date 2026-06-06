package com.harsh.swipey.data.repository

import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.remote.SwipeBody
import com.harsh.swipey.data.remote.SwipeyApi
import com.harsh.swipey.data.remote.toDomain
import kotlinx.coroutines.flow.StateFlow

/**
 * Gateway between the app and the backend swipe API. The UI keeps observing
 * [SavedIdeasStore]; this repository keeps that cache in sync with the server.
 */
interface IdeaRepository {

    /** The shared saved-ideas cache the whole UI observes. */
    val saved: StateFlow<List<IdeaModel>>

    /** Pending deck for the swipe screen, optionally filtered to one niche. */
    suspend fun fetchDeck(limit: Int = 20, niche: String? = null): List<IdeaModel>

    /** Pull the server's saved list into the local cache. */
    suspend fun refreshSaved()

    /** Swipe right / ♡ — approve + save. Updates the cache optimistically. */
    suspend fun like(idea: IdeaModel)

    /** Swipe up / ↑ — save. Updates the cache optimistically. */
    suspend fun save(idea: IdeaModel)

    /** Swipe left / ✕ — reject. Records the swipe; no cache change. */
    suspend fun dismiss(idea: IdeaModel)

    /** Remove a saved idea. */
    suspend fun unsave(id: String)

    /** Persist edited tags for an idea; updates the cached copy too. */
    suspend fun updateTags(ideaId: String, tags: List<String>)

    /** Expand one idea into a full script (Gemini). Returns the full text. */
    suspend fun expandEcho(ideaId: String): String
}

/** Retrofit-backed implementation. */
class RemoteIdeaRepository(
    private val api: SwipeyApi,
) : IdeaRepository {

    override val saved: StateFlow<List<IdeaModel>> = SavedIdeasStore.saved

    override suspend fun fetchDeck(limit: Int, niche: String?): List<IdeaModel> =
        api.getDeck(limit, niche).map { it.toDomain() }

    override suspend fun refreshSaved() {
        val list = api.getSaved().map { it.toDomain() }
        SavedIdeasStore.replaceAll(list)
    }

    override suspend fun like(idea: IdeaModel) {
        // Optimistic cache update first, then persist; the next deck/saved refresh reconciles.
        SavedIdeasStore.add(idea)
        api.swipe(idea.id, SwipeBody("approve"))
    }

    override suspend fun save(idea: IdeaModel) {
        SavedIdeasStore.add(idea)
        api.swipe(idea.id, SwipeBody("save"))
    }

    override suspend fun dismiss(idea: IdeaModel) {
        api.swipe(idea.id, SwipeBody("reject"))
    }

    override suspend fun unsave(id: String) {
        SavedIdeasStore.remove(id)
        api.unsave(id)
    }

    override suspend fun updateTags(ideaId: String, tags: List<String>) {
        val updated = api.updateTags(ideaId, com.harsh.swipey.data.remote.TagsBody(tags)).toDomain()
        SavedIdeasStore.updateTags(updated.id, updated.tags)
    }

    override suspend fun expandEcho(ideaId: String): String =
        api.echo(ideaId).fullText
}
