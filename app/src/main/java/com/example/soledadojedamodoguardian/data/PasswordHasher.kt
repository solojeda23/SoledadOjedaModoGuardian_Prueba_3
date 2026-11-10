package com.example.soledadojedamodoguardian.data

import java.security.SecureRandom
import android.util.Base64
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val ITERATIONS = 210_000
    private const val KEY_LENGTH = 256

    fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)

        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP
        )
    }

    fun generarHash(
        password: String,
        salt: String
    ): String {
        val saltBytes = Base64.decode(salt, Base64.NO_WRAP)

        val spec = PBEKeySpec(
            password.toCharArray(),
            saltBytes,
            ITERATIONS,
            KEY_LENGTH
        )

        return try {
            val factory = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            )

            val hash = factory.generateSecret(spec).encoded

            Base64.encodeToString(
                hash,
                Base64.NO_WRAP
            )
        } finally {
            spec.clearPassword()
        }
    }

    fun verificarPassword(
        password: String,
        salt: String,
        hashGuardado: String
    ): Boolean {
        val hashCalculado = generarHash(password, salt)

        return MessageDigest.isEqual(
            Base64.decode(hashCalculado, Base64.NO_WRAP),
            Base64.decode(hashGuardado, Base64.NO_WRAP)
        )
    }
}
