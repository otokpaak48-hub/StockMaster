package com.stockmaster.app.util

import java.security.MessageDigest
import java.security.SecureRandom

/** Hashes passwords with salt — plain-text passwords are FORBIDDEN here! */
object PasswordHasher {

    /** Random 16-byte salt, returned as hex text */
    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt.toHex()
    }

    /** Fingerprint of salt + password */
    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest((salt + password).toByteArray()).toHex()
    }

    /** Does the typed password match the stored fingerprint? */
    fun verify(password: String, salt: String, storedHash: String): Boolean {
        return hash(password, salt) == storedHash
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
