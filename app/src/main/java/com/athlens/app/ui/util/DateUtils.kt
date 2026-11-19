package com.athlens.app.ui.util

import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH)

    fun calculateAge(epochMillis: Long): Int {
        if (epochMillis <= 0L) return 0
        return try {
            val dob = Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val today = LocalDate.now()
            Period.between(dob, today).years.coerceAtLeast(0)
        } catch (e: Exception) {
            0
        }
    }

    fun isMinor(epochMillis: Long): Boolean {
        if (epochMillis <= 0L) return false
        return calculateAge(epochMillis) < 18
    }

    fun formatDate(epochMillis: Long): String {
        if (epochMillis <= 0L) return "Select Date"
        return try {
            val instant = Instant.ofEpochMilli(epochMillis)
            instant.atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    fun formatDateTime(epochMillis: Long): String {
        if (epochMillis <= 0L) return "Recent"
        return try {
            val instant = Instant.ofEpochMilli(epochMillis)
            instant.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
        } catch (e: Exception) {
            "Recent"
        }
    }

    // Default 16-year-old grassroots athlete date of birth for smooth UX
    fun defaultGrassrootsDobMillis(): Long {
        val sixteenYearsAgo = LocalDate.now().minusYears(16)
        return sixteenYearsAgo.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    val TAMIL_NADU_DISTRICTS = listOf(
        "Coimbatore",
        "Chennai",
        "Madurai",
        "Salem",
        "Tiruchirappalli",
        "Tirunelveli",
        "Erode",
        "Dindigul",
        "Thanjavur",
        "Kanchipuram",
        "Tiruvallur",
        "Cuddalore",
        "Vellore",
        "Villupuram",
        "Tiruvannamalai",
        "Nagapattinam",
        "Namakkal",
        "Karur",
        "Pudukkottai",
        "Sivaganga",
        "Virudhunagar",
        "Ramanathapuram",
        "Thoothukudi",
        "Kanyakumari",
        "Dharmapuri",
        "Krishnagiri",
        "The Nilgiris",
        "Theni",
        "Perambalur",
        "Ariyalur",
        "Tiruppur",
        "Tenkasi",
        "Tirupathur",
        "Ranipet",
        "Chengalpattu",
        "Kallakurichi",
        "Mayiladuthurai"
    )
}
