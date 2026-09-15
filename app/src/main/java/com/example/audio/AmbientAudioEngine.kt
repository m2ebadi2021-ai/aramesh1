package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Random
import kotlin.math.sin

enum class AmbientSoundType(val id: String, val titleFa: String, val titleEn: String, val icon: String) {
    RAIN("rain", "باران آرام", "Gentle Rain", "🌧️"),
    OCEAN("ocean", "امواج اقیانوس", "Ocean Waves", "🌊"),
    FIRE("fire", "آتش هیزمی", "Campfire", "🔥"),
    FOREST("forest", "جنگل سرسبز", "Green Forest", "🌲")
}

class AmbientAudioEngine(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var customMediaPlayer: MediaPlayer? = null

    // Track state for each sound: volume 0f to 1f and active status
    private val soundJobs = mutableMapOf<AmbientSoundType, Job?>()
    private val soundVolumes = mutableMapOf<AmbientSoundType, Float>().apply {
        AmbientSoundType.values().forEach { put(it, 0f) }
    }

    private val _activeSoundType = MutableStateFlow<AmbientSoundType?>(null)
    val activeSoundType: StateFlow<AmbientSoundType?> = _activeSoundType.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun playAmbient(type: AmbientSoundType) {
        stopAll()
        _activeSoundType.value = type
        _isPlaying.value = true
        setVolume(type, 0.8f)
    }

    fun stopAmbient() {
        stopAll()
        _activeSoundType.value = null
        _isPlaying.value = false
    }

    fun setVolume(type: AmbientSoundType, volume: Float) {
        soundVolumes[type] = volume.coerceIn(0f, 1f)
        if (volume > 0.01f) {
            if (soundJobs[type]?.isActive != true) {
                startSoundGenerator(type)
            }
        } else {
            soundJobs[type]?.cancel()
            soundJobs[type] = null
        }
    }

    fun getVolume(type: AmbientSoundType): Float = soundVolumes[type] ?: 0f

    fun stopAll() {
        soundJobs.values.forEach { it?.cancel() }
        soundJobs.clear()
        soundVolumes.keys.forEach { soundVolumes[it] = 0f }
        _activeSoundType.value = null
        _isPlaying.value = false
        customMediaPlayer?.stop()
        customMediaPlayer?.release()
        customMediaPlayer = null
    }

    fun playBellChime() {
        scope.launch {
            generateBellChime()
        }
    }

    fun playTibetanBowl() {
        scope.launch {
            generateSingingBowl()
        }
    }

    fun playSessionComplete() {
        scope.launch {
            generateSessionCompleteChimes()
        }
    }

    fun playBreathPhaseCue(phase: com.example.ui.canvas.BreathPhase, withVoice: Boolean) {
        val announcement = when (phase) {
            com.example.ui.canvas.BreathPhase.INHALE -> "دم"
            com.example.ui.canvas.BreathPhase.HOLD_IN -> "حبس"
            com.example.ui.canvas.BreathPhase.EXHALE -> "بازدم"
            com.example.ui.canvas.BreathPhase.HOLD_OUT -> "آرامش"
        }

        if (withVoice && isTtsReady && tts != null) {
            try {
                tts?.speak(announcement, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "phase_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                playPhaseTone(phase)
            }
        } else {
            playPhaseTone(phase)
        }
    }

    private fun playPhaseTone(phase: com.example.ui.canvas.BreathPhase) {
        scope.launch {
            when (phase) {
                com.example.ui.canvas.BreathPhase.INHALE -> generateSynthesizedChime(528.0, 1.2, 1.8) // Solfeggio 528Hz Love/Transformation
                com.example.ui.canvas.BreathPhase.HOLD_IN -> generateSynthesizedChime(432.0, 1.0, 2.2) // Sacred 432Hz Calm
                com.example.ui.canvas.BreathPhase.EXHALE -> generateSynthesizedChime(396.0, 1.5, 1.5) // Solfeggio 396Hz Liberation/Release
                com.example.ui.canvas.BreathPhase.HOLD_OUT -> generateSynthesizedChime(320.0, 0.8, 2.5) // Deep ground
            }
        }
    }

    fun speakAnnouncement(text: String, fallbackTone: Boolean = true) {
        if (isTtsReady && tts != null) {
            try {
                tts?.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "announcement_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                if (fallbackTone) playBellChime()
            }
        } else if (fallbackTone) {
            playBellChime()
        }
    }

    private var tts: android.speech.tts.TextToSpeech? = null
    private var isTtsReady = false

    init {
        try {
            tts = android.speech.tts.TextToSpeech(context) { status ->
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    isTtsReady = true
                    tts?.setPitch(0.95f)
                    tts?.setSpeechRate(0.85f)
                }
            }
        } catch (e: Exception) {
            // TTS unavailable, pure synthesis fallback active
        }
    }

    fun playCustomAudio(uri: Uri) {
        try {
            customMediaPlayer?.stop()
            customMediaPlayer?.release()
            customMediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopCustomAudio() {
        customMediaPlayer?.stop()
        customMediaPlayer?.release()
        customMediaPlayer = null
    }

    private fun startSoundGenerator(type: AmbientSoundType) {
        soundJobs[type]?.cancel()
        soundJobs[type] = scope.launch {
            when (type) {
                AmbientSoundType.RAIN -> generateRainStream()
                AmbientSoundType.OCEAN -> generateOceanStream()
                AmbientSoundType.FIRE -> generateFireStream()
                AmbientSoundType.FOREST -> generateForestStream()
            }
        }
    }

    private fun generateRainStream() {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        track.play()
        val random = Random()
        val buffer = ShortArray(bufferSize)
        var lastOut = 0.0

        try {
            while (scope.isActive && (soundVolumes[AmbientSoundType.RAIN] ?: 0f) > 0.01f) {
                val vol = soundVolumes[AmbientSoundType.RAIN] ?: 0f
                for (i in buffer.indices) {
                    val white = (random.nextDouble() * 2.0 - 1.0)
                    // Pink / Brown noise filter for soft rainfall
                    lastOut = (lastOut + (0.02 * white)) / 1.02
                    buffer[i] = (lastOut * 18000 * vol).toInt().coerceIn(-32767, 32767).toShort()
                }
                track.write(buffer, 0, buffer.size)
            }
        } catch (e: Exception) {
            // cancelled
        } finally {
            track.stop()
            track.release()
        }
    }

    private fun generateOceanStream() {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        track.play()
        val random = Random()
        val buffer = ShortArray(bufferSize)
        var phase = 0.0
        var b0 = 0.0
        var b1 = 0.0
        var b2 = 0.0

        try {
            while (scope.isActive && (soundVolumes[AmbientSoundType.OCEAN] ?: 0f) > 0.01f) {
                val vol = soundVolumes[AmbientSoundType.OCEAN] ?: 0f
                for (i in buffer.indices) {
                    val white = random.nextDouble() * 2.0 - 1.0
                    b0 = 0.99765 * b0 + white * 0.0990460
                    b1 = 0.96300 * b1 + white * 0.1965223
                    b2 = 0.57000 * b2 + white * 0.5500000
                    val pink = b0 + b1 + b2 + white * 0.5362

                    // Wave surge envelope: ~0.15 Hz slow ocean ebb and flow
                    phase += 0.12 * 2.0 * Math.PI / sampleRate
                    val envelope = 0.4 + 0.6 * (0.5 + 0.5 * sin(phase))

                    buffer[i] = (pink * 3500 * envelope * vol).toInt().coerceIn(-32767, 32767).toShort()
                }
                track.write(buffer, 0, buffer.size)
            }
        } catch (e: Exception) {
            // cancelled
        } finally {
            track.stop()
            track.release()
        }
    }

    private fun generateFireStream() {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        track.play()
        val random = Random()
        val buffer = ShortArray(bufferSize)
        var lowRoar = 0.0

        try {
            while (scope.isActive && (soundVolumes[AmbientSoundType.FIRE] ?: 0f) > 0.01f) {
                val vol = soundVolumes[AmbientSoundType.FIRE] ?: 0f
                for (i in buffer.indices) {
                    val white = random.nextDouble() * 2.0 - 1.0
                    lowRoar = (lowRoar + 0.015 * white) / 1.015
                    var sample = lowRoar * 7000

                    // Random crackles and pops
                    if (random.nextDouble() < 0.0015) {
                        sample += (random.nextDouble() * 2.0 - 1.0) * 16000
                    }
                    buffer[i] = (sample * vol).toInt().coerceIn(-32767, 32767).toShort()
                }
                track.write(buffer, 0, buffer.size)
            }
        } catch (e: Exception) {
            // cancelled
        } finally {
            track.stop()
            track.release()
        }
    }

    private fun generateForestStream() {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        track.play()
        val random = Random()
        val buffer = ShortArray(bufferSize)
        var rustle = 0.0

        try {
            while (scope.isActive && (soundVolumes[AmbientSoundType.FOREST] ?: 0f) > 0.01f) {
                val vol = soundVolumes[AmbientSoundType.FOREST] ?: 0f
                for (i in buffer.indices) {
                    val white = random.nextDouble() * 2.0 - 1.0
                    rustle = (rustle + 0.008 * white) / 1.008
                    buffer[i] = (rustle * 6500 * vol).toInt().coerceIn(-32767, 32767).toShort()
                }
                track.write(buffer, 0, buffer.size)
            }
        } catch (e: Exception) {
            // cancelled
        } finally {
            track.stop()
            track.release()
        }
    }

    private fun generateBellChime() {
        val sampleRate = 44100
        val durationSec = 3.5
        val numSamples = (sampleRate * durationSec).toInt()
        val samples = ShortArray(numSamples)

        val freq1 = 528.0 // Solfeggio Love / Healing tone
        val freq2 = 1056.0
        val freq3 = 1584.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = Math.exp(-t * 1.6) // smooth exponential bell decay
            val wave = 0.7 * sin(2.0 * Math.PI * freq1 * t) +
                    0.2 * sin(2.0 * Math.PI * freq2 * t) +
                    0.1 * sin(2.0 * Math.PI * freq3 * t)
            samples[i] = (wave * decay * 28000).toInt().coerceIn(-32767, 32767).toShort()
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(samples, 0, samples.size)
        track.play()
        Thread.sleep((durationSec * 1000).toLong())
        track.stop()
        track.release()
    }

    private fun generateSynthesizedChime(baseFreq: Double, durationSec: Double, decayRate: Double) {
        val sampleRate = 22050
        val numSamples = (sampleRate * durationSec).toInt()
        val samples = ShortArray(numSamples)

        val freqHarmonic = baseFreq * 2.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = Math.exp(-t * decayRate)
            val wave = 0.75 * sin(2.0 * Math.PI * baseFreq * t) +
                    0.25 * sin(2.0 * Math.PI * freqHarmonic * t)
            samples[i] = (wave * decay * 26000).toInt().coerceIn(-32767, 32767).toShort()
        }

        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep((durationSec * 1000).toLong())
            track.stop()
            track.release()
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun generateSingingBowl() {
        val sampleRate = 44100
        val durationSec = 4.0
        val numSamples = (sampleRate * durationSec).toInt()
        val samples = ShortArray(numSamples)

        // Tibetan singing bowl rich partials
        val f0 = 216.0 // Warm fundamental
        val f1 = 432.0 // First harmonic
        val f2 = 864.0 // Shimmering overtone
        val f3 = 1296.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = Math.exp(-t * 0.9)
            // Modulated shimmer
            val tremolo = 1.0 + 0.05 * sin(2.0 * Math.PI * 4.0 * t)
            val wave = (0.5 * sin(2.0 * Math.PI * f0 * t) +
                    0.3 * sin(2.0 * Math.PI * f1 * t) +
                    0.15 * sin(2.0 * Math.PI * f2 * t) +
                    0.05 * sin(2.0 * Math.PI * f3 * t)) * tremolo
            samples[i] = (wave * decay * 28000).toInt().coerceIn(-32767, 32767).toShort()
        }

        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep((durationSec * 1000).toLong())
            track.stop()
            track.release()
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun generateSessionCompleteChimes() {
        try {
            generateSingingBowl()
            Thread.sleep(600)
            generateSingingBowl()
        } catch (e: Exception) {
            // ignore
        }
    }
}
