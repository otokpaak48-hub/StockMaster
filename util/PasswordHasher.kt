package com.stockmaster.app.util

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordHasher {

    // Random salt: two staff with the same PIN never share a hash
    fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Salted SHA-256: salt first, then PIN, digested to hex
    fun hash(pin: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest((salt + pin).toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    // Same recipe, same result? Door opens.
    fun verify(pin: String, salt: String, expectedHash: String): Boolean {
        return hash(pin, salt) == expectedHash
    }
}
