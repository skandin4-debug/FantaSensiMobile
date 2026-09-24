package com.fantasensi.mobile.auth

import android.content.Context
import android.content.SharedPreferences

object AuthSession {
    private const val PREFS = "fantasensi_auth"
    private const val KEY_LICENSE = "license"
    private const val KEY_SESSION = "sessionid"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(context: Context, license: String, sessionId: String) {
        prefs(context).edit()
            .putString(KEY_LICENSE, license)
            .putString(KEY_SESSION, sessionId)
            .apply()
    }

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun license(context: Context): String? = prefs(context).getString(KEY_LICENSE, null)

    fun sessionId(context: Context): String? = prefs(context).getString(KEY_SESSION, null)
}
