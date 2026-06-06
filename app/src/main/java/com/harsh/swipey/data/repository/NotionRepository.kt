package com.harsh.swipey.data.repository

import com.harsh.swipey.data.remote.SwipeyApi

data class NotionPushResult(
    val ideaId: String,
    val title: String,
    val notionUrl: String,
    val ok: Boolean,
    val error: String? = null,
)

interface NotionRepository {
    suspend fun isEnabled(): Boolean
    suspend fun pushIdea(ideaId: String): NotionPushResult
    suspend fun pushAll(): List<NotionPushResult>
}

class RemoteNotionRepository(private val api: SwipeyApi) : NotionRepository {

    override suspend fun isEnabled(): Boolean =
        runCatching { api.getNotionStatus().enabled }.getOrDefault(false)

    override suspend fun pushIdea(ideaId: String): NotionPushResult {
        val dto = api.pushToNotion(ideaId)
        return NotionPushResult(dto.ideaId, dto.title, dto.notionUrl, true)
    }

    override suspend fun pushAll(): List<NotionPushResult> =
        api.pushAllToNotion().map {
            NotionPushResult(
                ideaId = it.ideaId,
                title = it.title,
                notionUrl = it.notionUrl,
                ok = it.ok,
                error = it.error,
            )
        }
}
