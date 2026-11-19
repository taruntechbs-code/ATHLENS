package com.athlens.app.data.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class PassphraseManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    @Synchronized
    fun getOrCreatePassphrase(): ByteArray {
        val encryptedBase64 = prefs.getString(KEY_ENCRYPTED_PASSPHRASE, null)
        val ivBase64 = prefs.getString(KEY_PASSPHRASE_IV, null)

        return if (encryptedBase64 != null && ivBase64 != null) {
            try {
                decryptPassphrase(encryptedBase64, ivBase64)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decrypt existing DB passphrase, generating fresh one", e)
                generateAndPersistNewPassphrase()
            }
        } else {
            generateAndPersistNewPassphrase()
        }
    }

    private fun generateAndPersistNewPassphrase(): ByteArray {
        val rawPassphrase = ByteArray(32)
        SecureRandom().nextBytes(rawPassphrase)

        ensureMasterKeyExists()
        val masterKey = getMasterKey()

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, masterKey)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(rawPassphrase)

        prefs.edit()
            .putString(KEY_ENCRYPTED_PASSPHRASE, Base64.encodeToString(encrypted, Base64.NO_WRAP))
            .putString(KEY_PASSPHRASE_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
            .apply()

        return rawPassphrase
    }

    private fun decryptPassphrase(encryptedBase64: String, ivBase64: String): ByteArray {
        val masterKey = getMasterKey()
        val encrypted = Base64.decode(encryptedBase64, Base64.NO_WRAP)
        val iv = Base64.decode(ivBase64, Base64.NO_WRAP)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, masterKey, gcmSpec)
        return cipher.doFinal(encrypted)
    }

    private fun ensureMasterKeyExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEY_STORE
            )
            val keyGenSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()

            keyGenerator.init(keyGenSpec)
            keyGenerator.generateKey()
            Log.d(TAG, "Generated new 256-bit AES master key in Android Keystore")
        }
    }

    private fun getMasterKey(): SecretKey {
        ensureMasterKeyExists()
        return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    companion object {
        private const val TAG = "PassphraseManager"
        private const val PREFS_NAME = "athlens_security_prefs"
        private const val KEY_ENCRYPTED_PASSPHRASE = "athlens_encrypted_db_passphrase"
        private const val KEY_PASSPHRASE_IV = "athlens_db_passphrase_iv"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "AthlensDbMasterKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH_BITS = 128
    }
}
