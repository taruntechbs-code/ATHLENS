package com.athlens.app.di

import android.content.Context
import com.athlens.app.data.audio.VoiceCoachManager
import com.athlens.app.data.local.AthlensDatabase
import com.athlens.app.data.local.AthlensDatabaseFactory
import com.athlens.app.data.p2p.NearbyExchangeManager
import com.athlens.app.data.repository.AthleteRepository
import com.athlens.app.data.repository.AthleteRepositoryImpl
import com.athlens.app.data.repository.TrainingRepository
import com.athlens.app.data.repository.TrainingRepositoryImpl
import com.athlens.app.data.security.PassphraseManager
import com.athlens.app.data.security.PassportCryptoManager

interface AppContainer {
    val context: Context
    val passphraseManager: PassphraseManager
    val passportCryptoManager: PassportCryptoManager
    val database: AthlensDatabase
    val athleteRepository: AthleteRepository
    val trainingRepository: TrainingRepository
    fun createVoiceCoachManager(): VoiceCoachManager
    fun createNearbyExchangeManager(): NearbyExchangeManager
}

class DefaultAppContainer(override val context: Context) : AppContainer {

    override val passphraseManager: PassphraseManager by lazy {
        PassphraseManager(context)
    }

    override val passportCryptoManager: PassportCryptoManager by lazy {
        PassportCryptoManager()
    }

    override val database: AthlensDatabase by lazy {
        AthlensDatabaseFactory.create(context, passphraseManager)
    }

    override val athleteRepository: AthleteRepository by lazy {
        AthleteRepositoryImpl(
            database.athleteProfileDao(),
            database.growthMeasurementDao(),
            database.achievementDao(),
            database.receivedPassportDao()
        )
    }

    override val trainingRepository: TrainingRepository by lazy {
        TrainingRepositoryImpl(database.trainingSessionDao())
    }

    override fun createVoiceCoachManager(): VoiceCoachManager {
        return VoiceCoachManager(context)
    }

    override fun createNearbyExchangeManager(): NearbyExchangeManager {
        return NearbyExchangeManager(context, passportCryptoManager)
    }
}
