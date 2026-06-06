package com.harsh.swipey.data

import com.harsh.swipey.BuildConfig
import com.harsh.swipey.data.remote.SwipeyApi
import com.harsh.swipey.data.repository.IdeaRepository
import com.harsh.swipey.data.repository.ProfileRepository
import com.harsh.swipey.data.repository.RemoteIdeaRepository
import com.harsh.swipey.data.repository.RemoteProfileRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Minimal manual DI. Lazily builds the Retrofit API and the [IdeaRepository] singleton.
 * The single demo user is identified via the `X-User-Id` header (no auth in this phase).
 */
object ServiceLocator {

    private const val USER_ID = "demo"

    private val httpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-User-Id", USER_ID)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            // Gemini generation/echo can take a while — allow a generous read timeout.
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val api: SwipeyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SWIPEY_API_BASE)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SwipeyApi::class.java)
    }

    val ideaRepository: IdeaRepository by lazy { RemoteIdeaRepository(api) }

    val profileRepository: ProfileRepository by lazy { RemoteProfileRepository(api) }
}
