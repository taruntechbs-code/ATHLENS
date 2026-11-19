package com.athlens.app.data.local

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.athlens.app.data.security.PassphraseManager
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

object AthlensDatabaseFactory {
    private const val TAG = "AthlensDbFactory"

    fun create(context: Context, passphraseManager: PassphraseManager): AthlensDatabase {
        loadSqlCipherSafely()

        val passphrase = passphraseManager.getOrCreatePassphrase()
        val factory = SupportOpenHelperFactory(passphrase)

        return Room.databaseBuilder(
            context.applicationContext,
            AthlensDatabase::class.java,
            AthlensDatabase.DATABASE_NAME
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    private fun loadSqlCipherSafely() {
        try {
            System.loadLibrary("sqlcipher")
            Log.d(TAG, "SQLCipher native library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "SQLCipher native library already linked or loading: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error loading SQLCipher: ${e.message}")
        }
    }
}
