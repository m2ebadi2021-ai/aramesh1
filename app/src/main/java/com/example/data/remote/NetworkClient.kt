package com.example.data.remote

import com.example.data.remote.api.ArameshApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    const val BASE_URL = "https://taravatgroup.ir/apps2/apps/panel-aramesh-app_1789470886/"
    const val API_TOKEN = "bc7ff554a5695b008a4f8b05fa0c69e5"

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    val apiService: ArameshApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ArameshApiService::class.java)
    }

    /**
     * Resolves a media or image URL from the server.
     * Rule:
     * - If path is null or blank -> returns null
     * - If path starts with http:// or https:// -> returns as is
     * - Otherwise prepends BASE_URL
     */
    fun resolveUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        val trimmed = path.trim()
        if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
            return trimmed
        }
        val cleanPath = trimmed.removePrefix("/")
        return "$BASE_URL$cleanPath"
    }
}
