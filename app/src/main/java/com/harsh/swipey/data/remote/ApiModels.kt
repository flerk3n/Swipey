package com.harsh.swipey.data.remote

import com.google.gson.annotations.SerializedName
import com.harsh.swipey.data.model.IdeaModel

/**
 * Network DTOs for the Swipey FastAPI backend. Idea responses already match the app's
 * [IdeaModel] shape, so mapping is 1:1.
 */

data class IdeaDto(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val tags: List<String> = emptyList(),
)

fun IdeaDto.toDomain(): IdeaModel =
    IdeaModel(id = id, category = category, title = title, description = description, tags = tags)

/** Request body for POST /ideas/{id}/swipe. */
data class SwipeBody(val action: String)

data class SwipeResponseDto(
    val id: String,
    val action: String,
    val status: String,
    val saved: Boolean,
)

data class EchoResponseDto(
    val id: String,
    @SerializedName("full_text") val fullText: String,
    val generated: Boolean,
)

data class HealthDto(
    val status: String,
    @SerializedName("gemini_enabled") val geminiEnabled: Boolean,
    val model: String,
    @SerializedName("user_id") val userId: String,
)

/** Request body for PATCH /ideas/{id}/tags. */
data class TagsBody(val tags: List<String>)

data class SettingsDto(
    @SerializedName("push_notifications") val pushNotifications: Boolean = true,
    @SerializedName("daily_reminder") val dailyReminder: Boolean = true,
    @SerializedName("reduced_motion") val reducedMotion: Boolean = false,
    @SerializedName("notion_connected") val notionConnected: Boolean = false,
    @SerializedName("calendar_connected") val calendarConnected: Boolean = false,
)

data class ProfileDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("display_name") val displayName: String = "Editor",
    @SerializedName("niche_id") val nicheId: String? = null,
    @SerializedName("goal_id") val goalId: String? = null,
    val onboarded: Boolean = false,
    val settings: SettingsDto = SettingsDto(),
)

/** Partial update body for PUT /profile (null fields are left unchanged). */
data class ProfileUpdateBody(
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("niche_id") val nicheId: String? = null,
    @SerializedName("goal_id") val goalId: String? = null,
    val onboarded: Boolean? = null,
)
