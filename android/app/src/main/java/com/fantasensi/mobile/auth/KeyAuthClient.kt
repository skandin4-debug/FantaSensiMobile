package com.fantasensi.mobile.auth

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

enum class KaOutcome { OK, INVALID, UNREACHABLE }

data class KaResult(
    val outcome: KaOutcome,
    val message: String = "",
    val sessionId: String = ""
)

object KeyAuthClient {
    private const val TIMEOUT_MS = 12000

    private val qType = Obf.d("qOOjrOj+UBMsMzPWJ+FLirVRSPuUn2DLK/A9OL+dGFU=")
    private val qVer = Obf.d("fdaut7AYCBxtM5jgZNPaBaen2fSBmp9eB68rlHxava0=")
    private val qName = Obf.d("z3pKWUIDgCROw7j4/a8ZYGP4UTMXIJsfuak0JxHnckU=")
    private val qOwner = Obf.d("DdMnsKpE8Gfs8gfyEWh/aNXRk/k6lb8dxumu5xpQk0I=")
    private val qKey = Obf.d("FyEsqFceit130AF4cPW8kYEQa5AyuvF4KxUHILKy96Q=")
    private val qHwid = Obf.d("6NZ/SJK3m+RAfBSK1DBfwyg1YSNE0JtT5+HhXJ8NoKw=")
    private val qSession = Obf.d("zg4XXb5fnbQJ+3ToRdZ87QC64/ScBy4Oy0OAiJ74A7I=")
    private val tInit = Obf.d("zu7xjK53Rnf7VLwVEInTG8R8cMwmQmPd2Y9tQFBNHNM=")
    private val tLicense = Obf.d("TaDXljeDk+VgumfxxkPX6CBX/j4/AHjCYvFpRiNG88Y=")
    private val tCheck = Obf.d("+X7+zEEyXSTc2FIrFlhlS6E/WjeBXK7lcc7H5rdpNkc=")

    fun init(): KaResult {
        val url = AuthConfig.url + qType + tInit + qVer + AuthConfig.version +
            qName + urlEncode(AuthConfig.name) + qOwner + urlEncode(AuthConfig.ownerId)
        val body = request(url) ?: return KaResult(KaOutcome.UNREACHABLE, "Falha de conexão.")
        return try {
            val json = JSONObject(body)
            if (json.optBoolean("success")) {
                val sid = json.optString("sessionid")
                if (sid.isEmpty()) KaResult(KaOutcome.INVALID, "Sessão não retornada.")
                else KaResult(KaOutcome.OK, "Sessão iniciada.", sid)
            } else {
                KaResult(KaOutcome.INVALID, json.optString("message", "Falha na inicialização."))
            }
        } catch (_: Exception) {
            KaResult(KaOutcome.UNREACHABLE, "Falha de conexão.")
        }
    }

    fun license(key: String, hwid: String, sessionId: String): KaResult {
        val url = AuthConfig.url + qType + tLicense + qKey + urlEncode(key) +
            qHwid + urlEncode(hwid) + qSession + urlEncode(sessionId) +
            qName + urlEncode(AuthConfig.name) + qOwner + urlEncode(AuthConfig.ownerId)
        val body = request(url) ?: return KaResult(KaOutcome.UNREACHABLE, "Falha de conexão.")
        return try {
            val json = JSONObject(body)
            if (json.optBoolean("success")) {
                KaResult(KaOutcome.OK, "Autenticado.")
            } else {
                KaResult(KaOutcome.INVALID, json.optString("message", "Chave inválida."))
            }
        } catch (_: Exception) {
            KaResult(KaOutcome.UNREACHABLE, "Falha de conexão.")
        }
    }

    fun check(sessionId: String): KaResult {
        val url = AuthConfig.url + qType + tCheck + qSession + urlEncode(sessionId) +
            qName + urlEncode(AuthConfig.name) + qOwner + urlEncode(AuthConfig.ownerId)
        val body = request(url) ?: return KaResult(KaOutcome.UNREACHABLE, "Falha de conexão.")
        return try {
            val json = JSONObject(body)
            if (json.optBoolean("success")) {
                KaResult(KaOutcome.OK, json.optString("message"))
            } else {
                KaResult(KaOutcome.INVALID, json.optString("message", "Sessão inválida."))
            }
        } catch (_: Exception) {
            KaResult(KaOutcome.UNREACHABLE, "Falha de conexão.")
        }
    }

    private fun request(url: String): String? {
        return try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.setRequestProperty("User-Agent", "KeyAuth")
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() } ?: ""
            conn.disconnect()
            text
        } catch (_: Exception) {
            null
        }
    }

    private fun urlEncode(s: String): String = URLEncoder.encode(s, "UTF-8")
}
