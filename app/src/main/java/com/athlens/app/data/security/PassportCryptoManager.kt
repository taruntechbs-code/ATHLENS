package com.athlens.app.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec

class PassportCryptoManager {

    companion object {
        private const val TAG = "PassportCryptoManager"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "athlens_passport_signing_key"
        private const val SIGNING_ALGORITHM = "SHA256withECDSA"
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    init {
        ensureKeypairExists()
    }

    private fun ensureKeypairExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            try {
                val kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC,
                    ANDROID_KEYSTORE
                )
                val parameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                )
                    .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .build()

                kpg.initialize(parameterSpec)
                kpg.generateKeyPair()
                Log.i(TAG, "Hardware-backed ECDSA P-256 keypair created with alias $KEY_ALIAS")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate ECDSA keypair: ${e.message}", e)
            }
        }
    }

    fun getPublicKey(): PublicKey? {
        return keyStore.getCertificate(KEY_ALIAS)?.publicKey
    }

    fun getPublicKeyBase64(): String {
        val pubKey = getPublicKey() ?: return ""
        return Base64.encodeToString(pubKey.encoded, Base64.NO_WRAP)
    }

    fun getPublicKeyFingerprint(): String {
        return try {
            val pubKey = getPublicKey() ?: return "UNKNOWN"
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(pubKey.encoded)
            hash.take(6).joinToString("") { "%02X".format(it) }
        } catch (e: Exception) {
            "OFFLINE-KEY"
        }
    }

    fun signPayload(canonicalPayload: String): String {
        return try {
            val privateKey = keyStore.getKey(KEY_ALIAS, null) as? PrivateKey
                ?: throw IllegalStateException("Private key not found for alias $KEY_ALIAS")

            val signature = Signature.getInstance(SIGNING_ALGORITHM).apply {
                initSign(privateKey)
                update(canonicalPayload.toByteArray(Charsets.UTF_8))
            }
            val signatureBytes = signature.sign()
            Base64.encodeToString(signatureBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e(TAG, "Signing error: ${e.message}", e)
            ""
        }
    }

    fun verifySignature(
        canonicalPayload: String,
        signatureBase64: String,
        publicKeyBase64: String
    ): Boolean {
        return try {
            val keyBytes = Base64.decode(publicKeyBase64, Base64.NO_WRAP)
            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC)
            val publicKey = keyFactory.generatePublic(keySpec)

            val sigBytes = Base64.decode(signatureBase64, Base64.NO_WRAP)
            val verifier = Signature.getInstance(SIGNING_ALGORITHM).apply {
                initVerify(publicKey)
                update(canonicalPayload.toByteArray(Charsets.UTF_8))
            }
            verifier.verify(sigBytes)
        } catch (e: Exception) {
            Log.w(TAG, "Verification failed or rejected: ${e.message}")
            false
        }
    }
}
