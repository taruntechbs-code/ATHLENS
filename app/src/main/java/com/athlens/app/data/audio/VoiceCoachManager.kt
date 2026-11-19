package com.athlens.app.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.util.BilingualStrings
import java.util.Locale

class VoiceCoachManager(
    private val context: Context,
    private var language: Language = Language.TAMIL
) : TextToSpeech.OnInitListener {

    companion object {
        private const val TAG = "VoiceCoachManager"
        private const val MIN_COOLDOWN_MS = 3200L
        private const val UTTERANCE_ID = "athlens_coach_utterance"
    }

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isMuted = false
    private var lastSpokenCode: String = ""
    private var lastSpokenTimestampMs: Long = 0L

    init {
        initializeTts()
    }

    private fun initializeTts() {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            setupAudioAttributes()
            applyLanguage(language)
            tts?.setSpeechRate(1.02f)
            tts?.setPitch(1.0f)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d(TAG, "TTS playback started: $utteranceId")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d(TAG, "TTS playback completed: $utteranceId")
                }

                override fun onError(utteranceId: String?) {
                    Log.w(TAG, "TTS playback error on utterance: $utteranceId")
                }
            })
            Log.i(TAG, "TextToSpeech initialized successfully with language $language")
        } else {
            Log.e(TAG, "TextToSpeech initialization failed with status: $status")
            isInitialized = false
        }
    }

    private fun setupAudioAttributes() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            tts?.setAudioAttributes(audioAttributes)
        } catch (e: Exception) {
            Log.w(TAG, "Could not set custom AudioAttributes: ${e.message}")
        }
    }

    fun setLanguage(newLanguage: Language) {
        this.language = newLanguage
        if (isInitialized) {
            applyLanguage(newLanguage)
        }
    }

    private fun applyLanguage(lang: Language) {
        val targetLocale = when (lang) {
            Language.TAMIL -> Locale("ta", "IN")
            Language.ENGLISH -> Locale("en", "IN")
        }

        val result = tts?.setLanguage(targetLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(TAG, "Target locale $targetLocale not supported or missing data. Result code: $result")
            // Fallback strategy: if Tamil is not available, try generic Tamil or English
            if (lang == Language.TAMIL) {
                val genericTamil = Locale("ta")
                val genericResult = tts?.setLanguage(genericTamil)
                if (genericResult == TextToSpeech.LANG_MISSING_DATA || genericResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "Generic Tamil unavailable, falling back to Indian English")
                    tts?.setLanguage(Locale("en", "IN"))
                }
            } else {
                tts?.setLanguage(Locale.US)
            }
        } else {
            Log.i(TAG, "TTS configured with locale: $targetLocale")
        }
    }

    fun setMuted(muted: Boolean) {
        this.isMuted = muted
        if (muted) {
            stop()
        }
    }

    fun isMuted(): Boolean = isMuted

    /**
     * Speaks coaching feedback based on error code with debouncing.
     */
    fun speakCorrection(errorCode: String, force: Boolean = false) {
        if (isMuted || !isInitialized) return
        if (errorCode.isEmpty()) return

        val now = System.currentTimeMillis()
        val timeSinceLast = now - lastSpokenTimestampMs

        // Avoid continuous repetition of same error within cooldown
        if (!force) {
            if (errorCode == lastSpokenCode && timeSinceLast < MIN_COOLDOWN_MS) {
                return
            }
            if (timeSinceLast < 2200L) {
                return
            }
        }

        val cueText = BilingualStrings.getVoiceCue(errorCode, language)
        lastSpokenCode = errorCode
        lastSpokenTimestampMs = now

        speakInternal(cueText, TextToSpeech.QUEUE_FLUSH)
    }

    /**
     * Speaks session milestone events (session started, completed, etc.).
     */
    fun speakMilestone(text: String) {
        if (isMuted || !isInitialized) return
        lastSpokenTimestampMs = System.currentTimeMillis()
        speakInternal(text, TextToSpeech.QUEUE_FLUSH)
    }

    private fun speakInternal(text: String, queueMode: Int) {
        try {
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            }
            tts?.speak(text, queueMode, params, UTTERANCE_ID)
        } catch (e: Exception) {
            Log.e(TAG, "Error executing speak: ${e.message}", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping TTS: ${e.message}")
        }
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.w(TAG, "Error shutting down TTS: ${e.message}")
        }
    }
}
