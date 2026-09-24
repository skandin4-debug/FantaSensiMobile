package com.fantasensi.mobile.auth

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Obf {
    private val key = byteArrayOf(
        130.toByte(), 196.toByte(), 66.toByte(), 117.toByte(), 81.toByte(), 185.toByte(), 215.toByte(), 22.toByte(),
        157.toByte(), 228.toByte(), 77.toByte(), 101.toByte(), 85.toByte(), 95.toByte(), 254.toByte(), 255.toByte(),
        78.toByte(), 251.toByte(), 132.toByte(), 1.toByte(), 17.toByte(), 15.toByte(), 41.toByte(), 112.toByte(),
        233.toByte(), 80.toByte(), 2.toByte(), 14.toByte(), 192.toByte(), 81.toByte(), 163.toByte(), 24.toByte()
    )

    fun d(encoded: String): String {
        val raw = Base64.decode(encoded, Base64.NO_WRAP)
        val iv = raw.copyOfRange(0, 16)
        val cipherText = raw.copyOfRange(16, raw.size)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        return String(cipher.doFinal(cipherText), Charsets.UTF_8)
    }
}
