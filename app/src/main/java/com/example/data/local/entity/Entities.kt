package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gratitude")
data class GratitudeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String, // YYYY-MM-DD
    val body: String, // max 280 chars
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "checkins")
data class CheckInEntity(
    @PrimaryKey
    val date: String, // YYYY-MM-DD
    val mood: Int, // 1-5
    val energy: Int, // 1-3
    val sleep: Int, // 1-3
    val note: String = "", // max 600 chars
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "breathing")
data class BreathingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String, // YYYY-MM-DD
    val pattern: String,
    val cycles: Int,
    val durationSec: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "meditation")
data class MeditationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String, // YYYY-MM-DD
    val durationSec: Int,
    val guided: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "mindfulness")
data class MindfulnessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String, // YYYY-MM-DD
    val kind: String, // 'ground', 'body_scan', 'one_min'
    val body: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "letters")
data class LetterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String, // YYYY-MM-DD
    val title: String,
    val audience: String, // 'today', 'past', 'future'
    val body: String,
    val openDate: String, // YYYY-MM-DD
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "selflove")
data class SelfLoveEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String, // YYYY-MM-DD
    val kind: String, // 'task', 'mirror'
    val body: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "selfknow_q")
data class SelfKnowQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String,
    val question: String,
    val answer: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "selfknow_items")
data class SelfKnowItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val category: String, // 'values', 'strengths'
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "esteem_wins")
data class EsteemWinEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "esteem_items")
data class EsteemItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val kind: String, // 'quality', 'challenge'
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "esteem_logs")
data class EsteemLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val itemId: Long,
    val date: String,
    val completed: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String,
    val title: String,
    val body: String, // max 5000 chars
    val mood: Int = 3,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey
    val key: String,
    val unlockedAt: String // ISO or YYYY-MM-DD timestamp
)

@Entity(tableName = "server_media")
data class ServerMediaEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String?,
    val url: String?,
    val image: String?,
    val duration: Int = 0,
    val category: String?,
    val type: String, // "audio", "image", "video", "notification"
    val sortOrder: Int = 0,
    val updatedAt: String?,
    val fullMediaUrl: String?,
    val fullImageUrl: String?,
    val isRead: Boolean = false
)

