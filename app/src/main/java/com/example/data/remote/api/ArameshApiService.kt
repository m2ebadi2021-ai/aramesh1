package com.example.data.remote.api

import com.example.data.remote.dto.MediaApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ArameshApiService {

    @GET("index.php")
    suspend fun getMediaItems(
        @Query("api") api: Int = 1,
        @Query("token") token: String = "bc7ff554a5695b008a4f8b05fa0c69e5",
        @Query("type") type: String? = null
    ): MediaApiResponse
}
