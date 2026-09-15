package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaApiResponse(
    @Json(name = "ok") val ok: Boolean = false,
    @Json(name = "app_name") val appName: String? = null,
    @Json(name = "app_version") val appVersion: String? = null,
    @Json(name = "count") val count: Int = 0,
    @Json(name = "data") val data: List<MediaItemDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class MediaItemDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "title") val title: String = "",
    @Json(name = "description") val description: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "image") val image: String? = null,
    @Json(name = "duration") val duration: Int = 0,
    @Json(name = "category") val category: String? = null,
    @Json(name = "type") val type: String = "audio",
    @Json(name = "sort_order") val sortOrder: Int = 0,
    @Json(name = "updated_at") val updatedAt: String? = null
)
