package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface ArameshDao {

    // --- GRATITUDE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGratitude(gratitude: GratitudeEntity): Long

    @Update
    suspend fun updateGratitude(gratitude: GratitudeEntity)

    @Delete
    suspend fun deleteGratitude(gratitude: GratitudeEntity)

    @Query("DELETE FROM gratitude WHERE id = :id")
    suspend fun deleteGratitudeById(id: Long)

    @Query("SELECT * FROM gratitude ORDER BY date DESC, createdAt DESC")
    fun getAllGratitudes(): Flow<List<GratitudeEntity>>

    @Query("SELECT * FROM gratitude WHERE date = :date ORDER BY createdAt DESC")
    fun getGratitudesByDate(date: String): Flow<List<GratitudeEntity>>

    @Query("SELECT COUNT(*) FROM gratitude")
    fun getGratitudesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM gratitude")
    suspend fun getGratitudesCountDirect(): Int

    // --- CHECKIN ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntity)

    @Query("SELECT * FROM checkins WHERE date = :date LIMIT 1")
    fun getCheckInByDate(date: String): Flow<CheckInEntity?>

    @Query("SELECT * FROM checkins ORDER BY date DESC")
    fun getAllCheckIns(): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM checkins ORDER BY date DESC LIMIT :limit")
    fun getRecentCheckIns(limit: Int): Flow<List<CheckInEntity>>

    // --- BREATHING ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreathing(breathing: BreathingEntity): Long

    @Query("SELECT * FROM breathing ORDER BY createdAt DESC")
    fun getAllBreathing(): Flow<List<BreathingEntity>>

    @Query("SELECT COALESCE(SUM(cycles), 0) FROM breathing")
    fun getTotalBreathingCycles(): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationSec), 0) FROM breathing")
    fun getTotalBreathingSeconds(): Flow<Int>

    // --- MEDITATION ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeditation(meditation: MeditationEntity): Long

    @Query("SELECT * FROM meditation ORDER BY createdAt DESC")
    fun getAllMeditations(): Flow<List<MeditationEntity>>

    @Query("SELECT COALESCE(SUM(durationSec), 0) FROM meditation")
    fun getTotalMeditationSeconds(): Flow<Int>

    // --- MINDFULNESS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMindfulness(mindfulness: MindfulnessEntity): Long

    @Query("SELECT * FROM mindfulness ORDER BY createdAt DESC")
    fun getAllMindfulness(): Flow<List<MindfulnessEntity>>

    // --- LETTERS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: LetterEntity): Long

    @Update
    suspend fun updateLetter(letter: LetterEntity)

    @Delete
    suspend fun deleteLetter(letter: LetterEntity)

    @Query("SELECT * FROM letters ORDER BY date DESC, createdAt DESC")
    fun getAllLetters(): Flow<List<LetterEntity>>

    // --- SELF LOVE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelfLove(selfLove: SelfLoveEntity): Long

    @Query("SELECT * FROM selflove ORDER BY createdAt DESC")
    fun getAllSelfLove(): Flow<List<SelfLoveEntity>>

    // --- SELF KNOWLEDGE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelfKnowQuestion(question: SelfKnowQuestionEntity): Long

    @Query("SELECT * FROM selfknow_q ORDER BY date DESC")
    fun getAllSelfKnowQuestions(): Flow<List<SelfKnowQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelfKnowItem(item: SelfKnowItemEntity): Long

    @Delete
    suspend fun deleteSelfKnowItem(item: SelfKnowItemEntity)

    @Query("SELECT * FROM selfknow_items WHERE category = :category ORDER BY createdAt ASC")
    fun getSelfKnowItemsByCategory(category: String): Flow<List<SelfKnowItemEntity>>

    // --- SELF ESTEEM ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEsteemWin(win: EsteemWinEntity): Long

    @Query("SELECT * FROM esteem_wins ORDER BY date DESC, createdAt DESC")
    fun getAllEsteemWins(): Flow<List<EsteemWinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEsteemItem(item: EsteemItemEntity): Long

    @Query("SELECT * FROM esteem_items WHERE kind = :kind ORDER BY createdAt ASC")
    fun getEsteemItemsByKind(kind: String): Flow<List<EsteemItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEsteemLog(log: EsteemLogEntity): Long

    @Query("SELECT * FROM esteem_logs ORDER BY date DESC")
    fun getAllEsteemLogs(): Flow<List<EsteemLogEntity>>

    // --- JOURNAL ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity): Long

    @Update
    suspend fun updateJournal(journal: JournalEntity)

    @Delete
    suspend fun deleteJournal(journal: JournalEntity)

    @Query("SELECT * FROM journal ORDER BY date DESC, createdAt DESC")
    fun getAllJournals(): Flow<List<JournalEntity>>

    @Query("SELECT COUNT(*) FROM journal")
    fun getJournalCount(): Flow<Int>

    // --- BADGES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeEntity)

    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges WHERE `key` = :key LIMIT 1")
    suspend fun getBadgeByKey(key: String): BadgeEntity?
}
