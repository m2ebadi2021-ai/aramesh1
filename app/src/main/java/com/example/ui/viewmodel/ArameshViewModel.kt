package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientAudioEngine
import com.example.audio.AmbientSoundType
import com.example.data.local.ArameshDatabase
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.BreathingEntity
import com.example.data.local.entity.CheckInEntity
import com.example.data.local.entity.EsteemItemEntity
import com.example.data.local.entity.EsteemWinEntity
import com.example.data.local.entity.GratitudeEntity
import com.example.data.local.entity.JournalEntity
import com.example.data.local.entity.LetterEntity
import com.example.data.local.entity.MeditationEntity
import com.example.data.local.entity.MindfulnessEntity
import com.example.data.local.entity.SelfKnowItemEntity
import com.example.data.local.entity.SelfKnowQuestionEntity
import com.example.data.local.entity.SelfLoveEntity
import com.example.data.repository.ArameshRepository
import com.example.domain.model.BadgeCatalog
import com.example.domain.model.BadgeDefinition
import com.example.domain.model.GratitudeTreeState
import com.example.ui.theme.AppThemeSetting
import com.example.worker.DailyHabitReminderWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArameshViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ArameshDatabase.getInstance(application)
    val repository = ArameshRepository(database.arameshDao())
    val audioEngine = AmbientAudioEngine(application)

    // Current App Theme
    private val _themeSetting = MutableStateFlow(AppThemeSetting.CALM)
    val themeSetting: StateFlow<AppThemeSetting> = _themeSetting.asStateFlow()

    fun setTheme(theme: AppThemeSetting) {
        _themeSetting.value = theme
    }

    // Keep screen awake toggle
    private val _keepScreenAwake = MutableStateFlow(true)
    val keepScreenAwake: StateFlow<Boolean> = _keepScreenAwake.asStateFlow()

    fun setKeepScreenAwake(enabled: Boolean) {
        _keepScreenAwake.value = enabled
    }

    // Notification reminder toggle
    private val _dailyRemindersEnabled = MutableStateFlow(true)
    val dailyRemindersEnabled: StateFlow<Boolean> = _dailyRemindersEnabled.asStateFlow()

    fun setDailyRemindersEnabled(enabled: Boolean) {
        _dailyRemindersEnabled.value = enabled
        DailyHabitReminderWorker.scheduleDailyReminder(getApplication(), enabled)
    }

    // Badge Unlock Celebration Dialog
    private val _celebrationBadge = MutableStateFlow<BadgeDefinition?>(null)
    val celebrationBadge: StateFlow<BadgeDefinition?> = _celebrationBadge.asStateFlow()

    fun dismissCelebration() {
        _celebrationBadge.value = null
    }

    // --- GRATITUDES & TREE ---
    val allGratitudes: StateFlow<List<GratitudeEntity>> = repository.allGratitudes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gratitudeTreeState: StateFlow<GratitudeTreeState> = repository.gratitudeTreeState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GratitudeTreeState.calculate(0))

    fun addGratitude(body: String) {
        if (body.isBlank()) return
        viewModelScope.launch {
            repository.addGratitude(body)
            checkBadgeCelebration()
        }
    }

    fun updateGratitude(entity: GratitudeEntity) {
        viewModelScope.launch {
            repository.updateGratitude(entity)
        }
    }

    fun deleteGratitude(id: Long) {
        viewModelScope.launch {
            repository.deleteGratitude(id)
        }
    }

    // --- CHECK INS & STREAK ---
    val allCheckIns: StateFlow<List<CheckInEntity>> = repository.allCheckIns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCheckIn: StateFlow<CheckInEntity?> = repository.getTodayCheckIn()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentStreak: StateFlow<Int> = allCheckIns.combine(_themeSetting) { checkIns, _ ->
        repository.calculateStreak(checkIns)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun saveCheckIn(mood: Int, energy: Int, sleep: Int, note: String) {
        viewModelScope.launch {
            repository.saveCheckIn(mood, energy, sleep, note)
            checkBadgeCelebration()
        }
    }

    fun recordQuickMoodCheckIn(mood: Int, note: String = "") {
        viewModelScope.launch {
            repository.saveCheckIn(mood = mood, energy = mood, sleep = 3, note = note)
            audioEngine.playBellChime()
            triggerVibration()
            checkBadgeCelebration()
        }
    }

    // Active growth tab navigation request
    private val _targetGrowthTab = MutableStateFlow("mindfulness")
    val targetGrowthTab: StateFlow<String> = _targetGrowthTab.asStateFlow()

    fun navigateToGrowthTab(tabId: String) {
        _targetGrowthTab.value = tabId
    }

    // Daily Wisdom Shuffle
    val wisdomQuotes = listOf(
        Pair("مولانا جلال‌الدین بلخی", "درون تو، آبی زلال جاری است؛ غبارها را با سکوت فرو بنشان تا عمق جانت پدیدار شود."),
        Pair("حافظ شیرازی", "در انـدرون من خسته دل ندانم کـیست / که من خموشم و او در فغان و در غوغاست"),
        Pair("سعدی شیرازی", "تن آدمی شریف است به جان آدمیت / نه همین لباس زیباست نشان آدمیت"),
        Pair("خیام نیشابوری", "از دی که گذشت هیچ از او یاد مکن / فردا که نیامده‌ست فریاد مکن؛ بر نامده و گذشته بنیاد مکن / حالی خوش باش و عمر بر باد مکن"),
        Pair("ذن و ذهن‌آگاهی", "گذشته خاطره است و آینده رویا. تنها ثروت راستین تو، همین نفسی است که اکنون فرو می‌بری."),
        Pair("دکتر دیوید هاوکینز", "رهایی زمانی آغاز می‌شود که بپذیری احساسات مانند ابرها می‌آیند و می‌روند، اما تو آسمان بی‌انتهایی."),
        Pair("لائو تزو", "طبیعت هیچ‌گاه عجله نمی‌کند، با این حال همه چیز در زمان موعود به کمال می‌رسد."),
        Pair("شمس تبریزی", "صبر یعنی نگاه کردن به خار و دیدن گل؛ نگاه کردن به شب و دیدن سحرگاه.")
    )

    private val _currentWisdomIndex = MutableStateFlow(0)
    val currentWisdom: StateFlow<Pair<String, String>> = _currentWisdomIndex.map { idx ->
        wisdomQuotes[idx % wisdomQuotes.size]
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), wisdomQuotes[0])

    fun nextWisdomQuote() {
        _currentWisdomIndex.value = (_currentWisdomIndex.value + 1) % wisdomQuotes.size
    }

    fun triggerBreathHaptic(phase: com.example.ui.canvas.BreathPhase) {
        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val duration = when (phase) {
                    com.example.ui.canvas.BreathPhase.INHALE -> 40L
                    com.example.ui.canvas.BreathPhase.HOLD_IN -> 25L
                    com.example.ui.canvas.BreathPhase.EXHALE -> 60L
                    com.example.ui.canvas.BreathPhase.HOLD_OUT -> 20L
                }
                vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    // --- BREATHING ---
    val allBreathing: StateFlow<List<BreathingEntity>> = repository.allBreathing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBreathingCycles: StateFlow<Int> = repository.totalBreathingCycles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun recordBreathingSession(pattern: String, cycles: Int, durationSec: Int) {
        viewModelScope.launch {
            repository.recordBreathing(pattern, cycles, durationSec)
            audioEngine.playBellChime()
            checkBadgeCelebration()
        }
    }

    // --- MEDITATION ---
    val allMeditations: StateFlow<List<MeditationEntity>> = repository.allMeditations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalMeditationSeconds: StateFlow<Int> = repository.totalMeditationSeconds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun recordMeditationSession(durationSec: Int, guided: Boolean) {
        viewModelScope.launch {
            repository.recordMeditation(durationSec, guided)
            audioEngine.playBellChime()
            checkBadgeCelebration()
        }
    }

    // --- MINDFULNESS ---
    val allMindfulness: StateFlow<List<MindfulnessEntity>> = repository.allMindfulness
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun recordMindfulness(kind: String, body: String) {
        viewModelScope.launch {
            repository.recordMindfulness(kind, body)
            checkBadgeCelebration()
        }
    }

    // --- LETTERS ---
    val allLetters: StateFlow<List<LetterEntity>> = repository.allLetters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addLetter(title: String, audience: String, body: String, openDate: String) {
        viewModelScope.launch {
            repository.addLetter(title, audience, body, openDate)
            checkBadgeCelebration()
        }
    }

    fun deleteLetter(letter: LetterEntity) {
        viewModelScope.launch {
            repository.deleteLetter(letter)
        }
    }

    // --- SELF LOVE ---
    val allSelfLove: StateFlow<List<SelfLoveEntity>> = repository.allSelfLove
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun recordSelfLove(kind: String, body: String) {
        viewModelScope.launch {
            repository.recordSelfLove(kind, body)
            checkBadgeCelebration()
        }
    }

    // --- SELF KNOWLEDGE ---
    val allSelfKnowQuestions: StateFlow<List<SelfKnowQuestionEntity>> = repository.allSelfKnowQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coreValues: StateFlow<List<SelfKnowItemEntity>> = repository.coreValues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personalStrengths: StateFlow<List<SelfKnowItemEntity>> = repository.personalStrengths
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun answerSelfKnowQuestion(question: String, answer: String) {
        viewModelScope.launch {
            repository.answerQuestion(question, answer)
            checkBadgeCelebration()
        }
    }

    fun addSelfKnowItem(category: String, content: String) {
        viewModelScope.launch {
            repository.addSelfKnowItem(category, content)
            checkBadgeCelebration()
        }
    }

    fun deleteSelfKnowItem(item: SelfKnowItemEntity) {
        viewModelScope.launch {
            repository.removeSelfKnowItem(item)
        }
    }

    // --- SELF ESTEEM ---
    val allEsteemWins: StateFlow<List<EsteemWinEntity>> = repository.allEsteemWins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val esteemQualities: StateFlow<List<EsteemItemEntity>> = repository.esteemQualities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEsteemWin(title: String) {
        viewModelScope.launch {
            repository.addEsteemWin(title)
            checkBadgeCelebration()
        }
    }

    fun addEsteemQuality(title: String, description: String) {
        viewModelScope.launch {
            repository.addEsteemItem("quality", title, description)
            checkBadgeCelebration()
        }
    }

    // --- JOURNAL ---
    val allJournals: StateFlow<List<JournalEntity>> = repository.allJournals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveJournal(title: String, body: String, mood: Int, id: Long = 0L) {
        viewModelScope.launch {
            repository.saveJournal(title, body, mood, id)
            checkBadgeCelebration()
        }
    }

    fun deleteJournal(journal: JournalEntity) {
        viewModelScope.launch {
            repository.deleteJournal(journal)
        }
    }

    // --- BADGES ---
    val unlockedBadges: StateFlow<List<BadgeEntity>> = repository.unlockedBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private suspend fun checkBadgeCelebration() {
        val newlyUnlocked = repository.evaluateBadges()
        if (newlyUnlocked.isNotEmpty()) {
            val firstBadgeKey = newlyUnlocked.first().key
            val badgeDef = BadgeCatalog.allBadges.find { it.key == firstBadgeKey }
            if (badgeDef != null) {
                _celebrationBadge.value = badgeDef
                triggerVibration()
                audioEngine.playBellChime()
            }
        }
    }

    private fun triggerVibration() {
        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(350)
            }
        } catch (e: Exception) {
            // ignore if not supported
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stopAll()
    }
}
