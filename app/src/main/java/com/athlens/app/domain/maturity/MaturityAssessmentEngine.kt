package com.athlens.app.domain.maturity

import com.athlens.app.domain.model.GrowthMeasurement
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.TrainingSession
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class MaturityContextReport(
    val exactAgeYears: Int,
    val exactAgeMonths: Int,
    val raeQuarter: String, // Q1, Q2, Q3, Q4
    val isLateBorn: Boolean,
    val growthVelocityCmPerYear: Float?,
    val heightDeltaCm: Float?,
    val isRapidGrowthSpurt: Boolean,
    val progressionDeltaPercent: Int?,
    val latestHeightCm: Float?,
    val latestWeightKg: Float?,
    val totalMeasurements: Int
) {
    fun getSummaryBadge(lang: Language): String {
        val raePart = when (lang) {
            Language.ENGLISH -> "$raeQuarter ${if (isLateBorn) "(Late-Born)" else "(Early-Born)"}"
            Language.TAMIL -> "$raeQuarter ${if (isLateBorn) "(பிற்பகுதி பிறப்பு)" else "(முற்பகுதி பிறப்பு)"}"
            Language.HINDI -> "$raeQuarter ${if (isLateBorn) "(उत्तरार्ध जन्म)" else "(पूर्वार्ध जन्म)"}"
        }

        val progressionPart = if (progressionDeltaPercent != null) {
            val sign = if (progressionDeltaPercent >= 0) "+" else ""
            when (lang) {
                Language.ENGLISH -> " • $sign$progressionDeltaPercent% Form Progression"
                Language.TAMIL -> " • $sign$progressionDeltaPercent% படிவ முன்னேற்றம்"
                Language.HINDI -> " • $sign$progressionDeltaPercent% फॉर्म सुधार"
            }
        } else {
            ""
        }

        return "$raePart$progressionPart"
    }
}

object MaturityAssessmentEngine {

    fun assess(
        dateOfBirthMillis: Long,
        measurements: List<GrowthMeasurement>,
        sessions: List<TrainingSession>
    ): MaturityContextReport {
        // 1. Chronological Age Calculation (Years and Months)
        val dob = try {
            Instant.ofEpochMilli(dateOfBirthMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } catch (e: Exception) {
            LocalDate.now()
        }
        val now = LocalDate.now()
        val period = Period.between(dob, now)
        val years = period.years
        val months = period.months

        // 2. Relative Age Effect (RAE) Quarter (Standard Jan 1 cutoff)
        val birthMonth = dob.monthValue // 1..12
        val (raeQuarter, isLateBorn) = when (birthMonth) {
            in 1..3 -> "Q1" to false
            in 4..6 -> "Q2" to false
            in 7..9 -> "Q3" to true
            else -> "Q4" to true
        }

        // 3. Growth Velocity Context
        val sortedMeasurements = measurements.sortedBy { it.recordedAt }
        val totalMeasurements = sortedMeasurements.size
        var growthVelocity: Float? = null
        var heightDelta: Float? = null
        var isRapidSpurt = false

        if (sortedMeasurements.size >= 2) {
            val first = sortedMeasurements.first()
            val latest = sortedMeasurements.last()

            val deltaHeight = latest.heightCm - first.heightCm
            heightDelta = deltaHeight

            val deltaMillis = latest.recordedAt - first.recordedAt
            val deltaDays = TimeUnit.MILLISECONDS.toDays(deltaMillis).coerceAtLeast(1)

            val annualizedRate = (deltaHeight / deltaDays.toFloat()) * 365.25f
            growthVelocity = annualizedRate

            // Growth spurt threshold in adolescent athletes (> 7.5 cm/yr)
            if (annualizedRate >= 7.5f) {
                isRapidSpurt = true
            }
        }

        val latestMeasurement = sortedMeasurements.lastOrNull()

        // 4. Training Progression Delta Calculation
        // Compare early baseline sessions with recent sessions
        val progressionDelta = calculateProgressionDelta(sessions)

        return MaturityContextReport(
            exactAgeYears = years,
            exactAgeMonths = months,
            raeQuarter = raeQuarter,
            isLateBorn = isLateBorn,
            growthVelocityCmPerYear = growthVelocity,
            heightDeltaCm = heightDelta,
            isRapidGrowthSpurt = isRapidSpurt,
            progressionDeltaPercent = progressionDelta,
            latestHeightCm = latestMeasurement?.heightCm,
            latestWeightKg = latestMeasurement?.weightKg,
            totalMeasurements = totalMeasurements
        )
    }

    private fun calculateProgressionDelta(sessions: List<TrainingSession>): Int? {
        if (sessions.size < 2) return null

        val sorted = sessions.sortedBy { it.timestamp }
        val sampleSize = (sorted.size / 3).coerceIn(1, 4)

        val baselineAvg = sorted.take(sampleSize).map { it.formScore }.average()
        val recentAvg = sorted.takeLast(sampleSize).map { it.formScore }.average()

        if (baselineAvg <= 0.0) return null

        val delta = ((recentAvg - baselineAvg) / baselineAvg) * 100.0
        return delta.roundToInt()
    }
}
