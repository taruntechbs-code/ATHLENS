package com.athlens.app.domain.passport

import org.json.JSONObject

object PassportSerializer {

    fun serializeCanonical(payload: PassportPayload): String {
        val obj = JSONObject()
        obj.put("v", payload.version)
        obj.put("id", payload.athleteId)
        obj.put("n", payload.fullName)
        obj.put("d", payload.district)
        obj.put("s", payload.sport)
        obj.put("p", payload.playingPosition)
        obj.put("ageM", payload.ageMonths)
        obj.put("rae", payload.raeQuarter)
        obj.put("sess", payload.totalSessions)
        obj.put("avg", payload.avgScore)
        if (payload.progressionDelta != null) {
            obj.put("delta", payload.progressionDelta)
        } else {
            obj.put("delta", JSONObject.NULL)
        }
        obj.put("ach", payload.verifiedAchievements)
        obj.put("ts", payload.issuedAtSeconds)
        return obj.toString()
    }

    fun buildEnvelope(
        canonicalPayload: String,
        signatureBase64: String,
        publicKeyBase64: String
    ): String {
        val envelope = JSONObject()
        envelope.put("payload", canonicalPayload)
        envelope.put("sig", signatureBase64)
        envelope.put("pub", publicKeyBase64)
        return envelope.toString()
    }

    fun parseEnvelope(envelopeJson: String): Triple<String, String, String>? {
        return try {
            val json = JSONObject(envelopeJson)
            val payload = json.getString("payload")
            val sig = json.getString("sig")
            val pub = json.getString("pub")
            Triple(payload, sig, pub)
        } catch (e: Exception) {
            null
        }
    }

    fun parseCanonicalPayload(canonicalJson: String): PassportPayload? {
        return try {
            val obj = JSONObject(canonicalJson)
            PassportPayload(
                version = obj.optInt("v", 1),
                athleteId = obj.optString("id", ""),
                fullName = obj.optString("n", "Unknown Athlete"),
                district = obj.optString("d", "Coimbatore"),
                sport = obj.optString("s", "KABADDI"),
                playingPosition = obj.optString("p", "Raider"),
                ageMonths = obj.optInt("ageM", 0),
                raeQuarter = obj.optString("rae", "Q1"),
                totalSessions = obj.optInt("sess", 0),
                avgScore = obj.optInt("avg", 0),
                progressionDelta = if (obj.has("delta") && !obj.isNull("delta")) obj.optInt("delta") else null,
                verifiedAchievements = obj.optInt("ach", 0),
                issuedAtSeconds = obj.optLong("ts", System.currentTimeMillis() / 1000)
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Creates a tampered version of the payload (e.g. inflating form score)
     * without regenerating the signature, allowing judges/scouts to see cryptographic verification failure.
     */
    fun tamperPayload(canonicalPayload: String): String {
        return try {
            val obj = JSONObject(canonicalPayload)
            val currentAvg = obj.optInt("avg", 70)
            obj.put("avg", (currentAvg + 15).coerceAtMost(100))
            obj.put("ach", obj.optInt("ach", 0) + 2)
            obj.toString()
        } catch (e: Exception) {
            canonicalPayload + "_tampered"
        }
    }
}
