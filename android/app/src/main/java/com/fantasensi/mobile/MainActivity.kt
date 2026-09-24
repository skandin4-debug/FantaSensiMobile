package com.fantasensi.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.fantasensi.mobile.auth.AuthSession
import com.fantasensi.mobile.auth.Hwid
import com.fantasensi.mobile.auth.KaOutcome
import com.fantasensi.mobile.auth.KeyAuthClient
import com.fantasensi.mobile.ui.screens.DashboardScreen
import com.fantasensi.mobile.ui.screens.LoginScreen
import com.fantasensi.mobile.ui.theme.FantaSensiTheme
import com.fantasensi.mobile.ui.theme.FsOrange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FantaSensiTheme {
                RootScreen()
            }
        }
    }
}

private enum class AuthState { LOADING, LOGGED_OUT, LOGGED_IN }

@Composable
private fun RootScreen() {
    val context = LocalContext.current
    var state by remember { mutableStateOf(AuthState.LOADING) }

    LaunchedEffect(Unit) {
        val savedKey = AuthSession.license(context)
        state = if (savedKey != null) {
            val init = withContext(Dispatchers.IO) { KeyAuthClient.init() }
            if (init.outcome == KaOutcome.OK) {
                val lic = withContext(Dispatchers.IO) {
                    KeyAuthClient.license(savedKey, Hwid.get(context), init.sessionId)
                }
                if (lic.outcome == KaOutcome.OK) {
                    AuthSession.save(context, savedKey, init.sessionId)
                    AuthState.LOGGED_IN
                } else {
                    AuthState.LOGGED_OUT
                }
            } else {
                AuthState.LOGGED_OUT
            }
        } else {
            AuthState.LOGGED_OUT
        }
    }

    when (state) {
        AuthState.LOADING -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF08080B)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = FsOrange)
        }

        AuthState.LOGGED_OUT -> LoginScreen(
            onAuthorized = { _, _ -> state = AuthState.LOGGED_IN }
        )

        AuthState.LOGGED_IN -> DashboardScreen()
    }
}
