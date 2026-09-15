package com.example.data.repository

import com.example.data.local.dao.ArameshDao
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.BreathingEntity
import com.example.data.local.entity.CheckInEntity
import com.example.data.local.entity.EsteemItemEntity
import com.example.data.local.entity.EsteemLogEntity
import com.example.data.local.entity.EsteemWinEntity
import com.example.data.local.entity.GratitudeEntity
import com.example.data.local.entity.JournalEntity
import com.example.data.local.entity.LetterEntity
import com.example.data.local.entity.MeditationEntity
import com.example.data.local.entity.MindfulnessEntity
import com.example.data.local.entity.SelfKnowItemEntity
import com.example.data.local.entity.SelfKnowQuestionEntity
import com.example.data.local.entity.SelfLoveEntity
import com.example.data.local.entity.ServerMediaEntity
import com.example.data.remote.NetworkClient
import com.example.data.remote.dto.MediaItemDto
import com.example.domain.model.BadgeCatalog
import com.example.domain.model.GratitudeTreeState
import com.example.domain.model.JalaaliCalendarHelper
import com.example.domain.model.ServerMediaItem
import com.example.domain.model.ServerMediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ArameshRepository(private val dao: ArameshDao) {

    // --- GRATITUDE ---
    val allGratitudes: Flow<List<GratitudeEntity>> = dao.getAllGratitudes()
    val totalGratitudeCount: Flow<Int> = dao.getGratitudesCount()

    val gratitudeTreeState: Flow<GratitudeTreeState> = dao.getGratitudesCount().map { count ->
        GratitudeTreeState.calculate(count)
    }

    suspend fun addGratitude(body: String, date: String = JalaaliCalendarHelper.todayGregorianString()): Long {
        val id = dao.insertGratitude(
            GratitudeEntity(
                date = date,
                body = body.trim()
            )
        )
        evaluateBadges()
        return id
    }

    suspend fun updateGratitude(entity: GratitudeEntity) = dao.updateGratitude(entity)
    suspend fun deleteGratitude(id: Long) = dao.deleteGratitudeById(id)

    // --- CHECK IN ---
    val allCheckIns: Flow<List<CheckInEntity>> = dao.getAllCheckIns()
    val recentCheckIns: Flow<List<CheckInEntity>> = dao.getRecentCheckIns(7)

    fun getTodayCheckIn(date: String = JalaaliCalendarHelper.todayGregorianString()): Flow<CheckInEntity?> {
        return dao.getCheckInByDate(date)
    }

    suspend fun saveCheckIn(mood: Int, energy: Int, sleep: Int, note: String, date: String = JalaaliCalendarHelper.todayGregorianString()) {
        dao.insertCheckIn(
            CheckInEntity(
                date = date,
                mood = mood,
                energy = energy,
                sleep = sleep,
                note = note.trim()
            )
        )
        evaluateBadges()
    }

    // --- BREATHING ---
    val allBreathing: Flow<List<BreathingEntity>> = dao.getAllBreathing()
    val totalBreathingCycles: Flow<Int> = dao.getTotalBreathingCycles()
    val totalBreathingSeconds: Flow<Int> = dao.getTotalBreathingSeconds()

    suspend fun recordBreathing(pattern: String, cycles: Int, durationSec: Int) {
        dao.insertBreathing(
            BreathingEntity(
                date = JalaaliCalendarHelper.todayGregorianString(),
                pattern = pattern,
                cycles = cycles,
                durationSec = durationSec
            )
        )
        evaluateBadges()
    }

    // --- MEDITATION ---
    val allMeditations: Flow<List<MeditationEntity>> = dao.getAllMeditations()
    val totalMeditationSeconds: Flow<Int> = dao.getTotalMeditationSeconds()

    suspend fun recordMeditation(durationSec: Int, guided: Boolean) {
        dao.insertMeditation(
            MeditationEntity(
                date = JalaaliCalendarHelper.todayGregorianString(),
                durationSec = durationSec,
                guided = guided
            )
        )
        evaluateBadges()
    }

    // --- MINDFULNESS ---
    val allMindfulness: Flow<List<MindfulnessEntity>> = dao.getAllMindfulness()

    suspend fun recordMindfulness(kind: String, body: String) {
        dao.insertMindfulness(
            MindfulnessEntity(
                date = JalaaliCalendarHelper.todayGregorianString(),
                kind = kind,
                body = body
            )
        )
        evaluateBadges()
    }

    // --- LETTERS ---
    val allLetters: Flow<List<LetterEntity>> = dao.getAllLetters()

    suspend fun addLetter(title: String, audience: String, body: String, openDate: String) {
        dao.insertLetter(
            LetterEntity(
                date = JalaaliCalendarHelper.todayGregorianString(),
                title = title,
                audience = audience,
                body = body,
                openDate = openDate
            )
        )
        evaluateBadges()
    }

    suspend fun deleteLetter(letter: LetterEntity) = dao.deleteLetter(letter)

    // --- SELF LOVE ---
    val allSelfLove: Flow<List<SelfLoveEntity>> = dao.getAllSelfLove()

    suspend fun recordSelfLove(kind: String, body: String) {
        dao.insertSelfLove(
            SelfLoveEntity(
                date = JalaaliCalendarHelper.todayGregorianString(),
                kind = kind,
                body = body
            )
        )
        evaluateBadges()
    }

    // --- SELF KNOWLEDGE ---
    val allSelfKnowQuestions: Flow<List<SelfKnowQuestionEntity>> = dao.getAllSelfKnowQuestions()
    val coreValues: Flow<List<SelfKnowItemEntity>> = dao.getSelfKnowItemsByCategory("values")
    val personalStrengths: Flow<List<SelfKnowItemEntity>> = dao.getSelfKnowItemsByCategory("strengths")

    suspend fun answerQuestion(question: String, answer: String) {
        dao.insertSelfKnowQuestion(
            SelfKnowQuestionEntity(
                date = JalaaliCalendarHelper.todayGregorianString(),
                question = question,
                answer = answer
            )
        )
        evaluateBadges()
    }

    suspend fun addSelfKnowItem(category: String, content: String) {
        dao.insertSelfKnowItem(
            SelfKnowItemEntity(
                category = category,
                content = content.trim()
            )
        )
        evaluateBadges()
    }

    suspend fun removeSelfKnowItem(item: SelfKnowItemEntity) = dao.deleteSelfKnowItem(item)

    // --- SELF ESTEEM ---
    val allEsteemWins: Flow<List<EsteemWinEntity>> = dao.getAllEsteemWins()
    val esteemQualities: Flow<List<EsteemItemEntity>> = dao.getEsteemItemsByKind("quality")
    val esteemChallenges: Flow<List<EsteemItemEntity>> = dao.getEsteemItemsByKind("challenge")
    val esteemLogs: Flow<List<EsteemLogEntity>> = dao.getAllEsteemLogs()

    suspend fun addEsteemWin(title: String) {
        dao.insertEsteemWin(
            EsteemWinEntity(
                date = JalaaliCalendarHelper.todayGregorianString(),
                title = title.trim()
            )
        )
        evaluateBadges()
    }

    suspend fun addEsteemItem(kind: String, title: String, description: String = "") {
        dao.insertEsteemItem(
            EsteemItemEntity(
                kind = kind,
                title = title.trim(),
                description = description.trim()
            )
        )
        evaluateBadges()
    }

    // --- JOURNAL ---
    val allJournals: Flow<List<JournalEntity>> = dao.getAllJournals()
    val totalJournalCount: Flow<Int> = dao.getJournalCount()

    suspend fun saveJournal(title: String, body: String, mood: Int, id: Long = 0L) {
        if (id == 0L) {
            dao.insertJournal(
                JournalEntity(
                    date = JalaaliCalendarHelper.todayGregorianString(),
                    title = title.trim(),
                    body = body.trim(),
                    mood = mood
                )
            )
        } else {
            dao.updateJournal(
                JournalEntity(
                    id = id,
                    date = JalaaliCalendarHelper.todayGregorianString(),
                    title = title.trim(),
                    body = body.trim(),
                    mood = mood,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        evaluateBadges()
    }

    suspend fun deleteJournal(journal: JournalEntity) = dao.deleteJournal(journal)

    // --- BADGES & ACHIEVEMENTS ---
    val unlockedBadges: Flow<List<BadgeEntity>> = dao.getAllBadges()

    // Streak calculation helper
    fun calculateStreak(checkIns: List<CheckInEntity>): Int {
        if (checkIns.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sortedDates = checkIns.mapNotNull {
            try { sdf.parse(it.date) } catch (e: Exception) { null }
        }.sortedDescending().distinct()

        if (sortedDates.isEmpty()) return 0

        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val today = cal.time

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = cal.time

        val firstDate = sortedDates[0]
        if (firstDate != today && firstDate != yesterday) {
            return 0
        }

        var streak = 1
        var curr = firstDate
        for (i in 1 until sortedDates.size) {
            val prevCal = Calendar.getInstance().apply {
                time = curr
                add(Calendar.DAY_OF_YEAR, -1)
            }
            if (sortedDates[i] == prevCal.time) {
                streak++
                curr = sortedDates[i]
            } else {
                break
            }
        }
        return streak
    }

    // Dynamic badge unlock checker based on the 17 business logic rules
    suspend fun evaluateBadges(): List<BadgeEntity> {
        val newlyUnlocked = mutableListOf<BadgeEntity>()
        val todayStr = JalaaliCalendarHelper.todayGregorianString()

        val gratitudeCount = dao.getGratitudesCountDirect()
        val checkIns = dao.getAllCheckIns().first()
        val streak = calculateStreak(checkIns)
        val breathingCycles = dao.getTotalBreathingCycles().first()
        val meditationSeconds = dao.getTotalMeditationSeconds().first()
        val mindfulnessList = dao.getAllMindfulness().first()
        val lettersList = dao.getAllLetters().first()
        val selfLoveList = dao.getAllSelfLove().first()
        val valuesList = dao.getSelfKnowItemsByCategory("values").first()
        val winsList = dao.getAllEsteemWins().first()
        val journalCount = dao.getJournalCount().first()

        suspend fun unlockIf(key: String, condition: Boolean) {
            if (condition && dao.getBadgeByKey(key) == null) {
                val badge = BadgeEntity(key = key, unlockedAt = todayStr)
                dao.insertBadge(badge)
                newlyUnlocked.add(badge)
            }
        }

        unlockIf("FIRST_SEED", gratitudeCount >= 1)
        unlockIf("BLOSSOM_BLOOM", gratitudeCount >= 3)
        unlockIf("RED_HARVEST", gratitudeCount >= 9)
        unlockIf("GOLDEN_CROWN", gratitudeCount >= 27)
        unlockIf("FIRST_CHECKIN", checkIns.isNotEmpty())
        unlockIf("SEVEN_DAY_STREAK", streak >= 7)
        unlockIf("BREATH_ROOKIE", breathingCycles >= 1)
        unlockIf("PRANA_MASTER", breathingCycles >= 50)
        unlockIf("ZEN_BEGINNER", meditationSeconds >= 60)
        unlockIf("MINDFUL_HOUR", meditationSeconds >= 3600)
        unlockIf("SENSORY_GROUND", mindfulnessList.any { it.kind == "ground" })
        unlockIf("BODY_SANCTUARY", mindfulnessList.any { it.kind == "body_scan" })
        unlockIf("TIME_TRAVELER", lettersList.isNotEmpty())
        unlockIf("MIRROR_SOUL", selfLoveList.any { it.kind == "mirror" })
        unlockIf("INNER_COMPASS", valuesList.size >= 5)
        unlockIf("TRIUMPH_CHRONICLE", winsList.size >= 5)
        unlockIf("AUTHOR_OF_SELF", journalCount >= 10)

        return newlyUnlocked
    }

    // --- SERVER MEDIA & NOTIFICATIONS ---
    val allServerMedia: Flow<List<ServerMediaItem>> = dao.getAllServerMedia().map { list ->
        list.map { it.toDomain() }
    }

    val serverAudios: Flow<List<ServerMediaItem>> = dao.getServerMediaByType("audio").map { list ->
        list.map { it.toDomain() }
    }

    val serverImages: Flow<List<ServerMediaItem>> = dao.getServerMediaByType("image").map { list ->
        list.map { it.toDomain() }
    }

    val serverVideos: Flow<List<ServerMediaItem>> = dao.getServerMediaByType("video").map { list ->
        list.map { it.toDomain() }
    }

    val serverNotifications: Flow<List<ServerMediaItem>> = dao.getServerMediaByType("notification").map { list ->
        list.map { it.toDomain() }
    }

    val unreadNotificationCount: Flow<Int> = dao.getUnreadNotificationCount()

    suspend fun syncServerMedia(type: String? = null): Result<List<ServerMediaItem>> {
        return try {
            val response = NetworkClient.apiService.getMediaItems(
                api = 1,
                token = NetworkClient.API_TOKEN,
                type = type
            )
            if (response.ok) {
                val entities = response.data.map { dto ->
                    dto.toEntity()
                }
                if (type == null) {
                    dao.clearAllServerMedia()
                    dao.insertServerMediaItems(entities)
                } else {
                    dao.deleteServerMediaByType(type.lowercase().trim())
                    dao.insertServerMediaItems(entities)
                }
                Result.success(entities.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.error ?: "خطا در دریافت اطلاعات از سرور"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markMediaAsRead(id: Long) {
        dao.markMediaAsRead(id)
    }
}

private fun MediaItemDto.toEntity(): ServerMediaEntity {
    return ServerMediaEntity(
        id = id,
        title = title,
        description = description,
        url = url,
        image = image,
        duration = duration,
        category = category,
        type = type.lowercase().trim(),
        sortOrder = sortOrder,
        updatedAt = updatedAt,
        fullMediaUrl = NetworkClient.resolveUrl(url),
        fullImageUrl = NetworkClient.resolveUrl(image)
    )
}

private fun ServerMediaEntity.toDomain(): ServerMediaItem {
    return ServerMediaItem(
        id = id,
        title = title,
        description = description ?: "",
        rawUrl = url,
        rawImage = image,
        fullMediaUrl = fullMediaUrl,
        fullImageUrl = fullImageUrl,
        durationSeconds = duration,
        category = category ?: "عمومی",
        type = ServerMediaType.fromRaw(type),
        sortOrder = sortOrder,
        updatedAt = updatedAt ?: "",
        isRead = isRead
    )
}
