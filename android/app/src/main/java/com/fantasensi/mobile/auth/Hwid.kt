package com.fantasensi.mobile.auth

import android.content.Context
import android.provider.Settings
import java.security.MessageDigest

object Hwid {
    fun get(context: Context): String {
        return try {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "unknown"
            sha256(androidId)
        } catch (_: Exception) {
            sha256("unknown")
        }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
