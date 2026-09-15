package com.example.domain.model

import com.example.data.remote.NetworkClient

enum class ServerMediaType(val rawValue: String, val titleFa: String, val iconEmoji: String) {
    AUDIO("audio", "صوت‌ها", "🎧"),
    IMAGE("image", "عکس‌ها", "🖼️"),
    VIDEO("video", "ویدیوها", "🎬"),
    NOTIFICATION("notification", "اعلان‌ها", "🔔");

    companion object {
        fun fromRaw(value: String?): ServerMediaType {
            return when (value?.lowercase()?.trim()) {
                "audio" -> AUDIO
                "image" -> IMAGE
                "video" -> VIDEO
                "notification" -> NOTIFICATION
                else -> AUDIO
            }
        }
    }
}

data class ServerMediaItem(
    val id: Long,
    val title: String,
    val description: String,
    val rawUrl: String?,
    val rawImage: String?,
    val fullMediaUrl: String?,
    val fullImageUrl: String?,
    val durationSeconds: Int,
    val category: String,
    val type: ServerMediaType,
    val sortOrder: Int,
    val updatedAt: String,
    val isRead: Boolean = false
) {
    val formattedDuration: String
        get() {
            if (durationSeconds <= 0) return ""
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }

    val hasMedia: Boolean
        get() = !fullMediaUrl.isNullOrBlank()

    val hasImage: Boolean
        get() = !fullImageUrl.isNullOrBlank()
}
