package com.athlens.app.di

import android.content.Context
import com.athlens.app.data.audio.VoiceCoachManager
import com.athlens.app.data.local.AthlensDatabase
import com.athlens.app.data.local.AthlensDatabaseFactory
import com.athlens.app.data.repository.AthleteRepository
import com.athlens.app.data.repository.AthleteRepositoryImpl
import com.athlens.app.data.repository.TrainingRepository
import com.athlens.app.data.repository.TrainingRepositoryImpl
import com.athlens.app.data.security.PassphraseManager

interface AppContainer {
    val passphraseManager: PassphraseManager
    val database: AthlensDatabase
    val athleteRepository: AthleteRepository
    val trainingRepository: TrainingRepository
    fun createVoiceCoachManager(): VoiceCoachManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val passphraseManager: PassphraseManager by lazy {
        PassphraseManager(context)
    }

    override val database: AthlensDatabase by lazy {
        AthlensDatabaseFactory.create(context, passphraseManager)
    }

    override val athleteRepository: AthleteRepository by lazy {
        AthleteRepositoryImpl(database.athleteProfileDao(), database.growthMeasurementDao())
    }

    override val trainingRepository: TrainingRepository by lazy {
        TrainingRepositoryImpl(database.trainingSessionDao())
    }

    override fun createVoiceCoachManager(): VoiceCoachManager {
        return VoiceCoachManager(context)
    }
}
