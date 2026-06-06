package com.harsh.swipey.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit interface for the Swipey backend (`/api/v1`). */
interface SwipeyApi {

    @GET("api/v1/health")
    suspend fun health(): HealthDto

    @GET("api/v1/ideas/deck")
    suspend fun getDeck(
        @Query("limit") limit: Int = 20,
        @Query("niche") niche: String? = null,
    ): List<IdeaDto>

    @GET("api/v1/ideas/saved")
    suspend fun getSaved(): List<IdeaDto>

    @POST("api/v1/ideas/{id}/swipe")
    suspend fun swipe(@Path("id") id: String, @Body body: SwipeBody): SwipeResponseDto

    @DELETE("api/v1/ideas/{id}/saved")
    suspend fun unsave(@Path("id") id: String): SwipeResponseDto

    @PATCH("api/v1/ideas/{id}/tags")
    suspend fun updateTags(@Path("id") id: String, @Body body: TagsBody): IdeaDto

    @POST("api/v1/ideas/{id}/echo")
    suspend fun echo(@Path("id") id: String): EchoResponseDto

    @GET("api/v1/profile")
    suspend fun getProfile(): ProfileDto

    @PUT("api/v1/profile")
    suspend fun updateProfile(@Body body: ProfileUpdateBody): ProfileDto

    @GET("api/v1/profile/settings")
    suspend fun getSettings(): SettingsDto

    @PUT("api/v1/profile/settings")
    suspend fun updateSettings(@Body body: SettingsDto): SettingsDto

    @GET("api/v1/notion/status")
    suspend fun getNotionStatus(): NotionStatusDto

    @POST("api/v1/ideas/{id}/notion")
    suspend fun pushToNotion(@Path("id") id: String): NotionPushDto

    @POST("api/v1/saved/notion")
    suspend fun pushAllToNotion(): List<NotionPushDto>
}
