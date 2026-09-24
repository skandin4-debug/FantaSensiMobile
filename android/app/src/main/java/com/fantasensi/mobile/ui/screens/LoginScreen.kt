package com.fantasensi.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fantasensi.mobile.R
import com.fantasensi.mobile.auth.AuthSession
import com.fantasensi.mobile.auth.Hwid
import com.fantasensi.mobile.auth.KaOutcome
import com.fantasensi.mobile.auth.KeyAuthClient
import com.fantasensi.mobile.ui.components.FantaGradientText
import com.fantasensi.mobile.ui.components.PrimaryButton
import com.fantasensi.mobile.ui.theme.FsDanger
import com.fantasensi.mobile.ui.theme.FsOrange
import com.fantasensi.mobile.ui.theme.FsTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(onAuthorized: (String, String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var key by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    val doLogin: () -> Unit = {
        val k = key.trim()
        if (k.isEmpty()) {
            status = "Coloque sua chave de ativação para continuar."
            error = true
        } else {
            busy = true
            status = "Validando chave... aguarde."
            error = false
            scope.launch {
                val init = withContext(Dispatchers.IO) { KeyAuthClient.init() }
                if (init.outcome != KaOutcome.OK) {
                    status = init.message
                    error = true
                    busy = false
                } else {
                    val lic = withContext(Dispatchers.IO) {
                        KeyAuthClient.license(k, Hwid.get(context), init.sessionId)
                    }
                    when (lic.outcome) {
                        KaOutcome.OK -> {
                            AuthSession.save(context, k, init.sessionId)
                            onAuthorized(k, init.sessionId)
                        }
                        else -> {
                            status = lic.message
                            error = true
                            busy = false
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08080B)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x14FFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(18.dp))
                    .padding(7.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(14.dp))

            FantaGradientText(text = "FantaSensi", fontSize = 30)

            Spacer(Modifier.height(7.dp))

            Text(
                text = "Entre com a chave de ativação para liberar o aplicativo",
                style = MaterialTheme.typography.bodyMedium,
                color = FsTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(26.dp))

            Text(
                text = "CHAVE DE ATIVAÇÃO",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFC9FFFFFF),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp)
            )

            Spacer(Modifier.height(7.dp))

            OutlinedTextField(
                value = key,
                onValueChange = { key = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !busy,
                placeholder = {
                    Text("Cole aqui sua chave de ativação", color = Color(0xFF8C8A93))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Key,
                        contentDescription = null,
                        tint = FsOrange
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = { doLogin() }),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FsOrange,
                    unfocusedBorderColor = Color(0x38FFFFFF),
                    focusedContainerColor = Color(0x14000000),
                    unfocusedContainerColor = Color(0x14000000),
                    cursorColor = FsOrange,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(Modifier.height(14.dp))

            PrimaryButton(
                text = if (busy) "VALIDANDO..." else "ACESSAR",
                onClick = doLogin,
                enabled = !busy,
                loading = busy
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = if (error) FsDanger else FsTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "v1.0 · FantaSensi",
                style = MaterialTheme.typography.labelMedium,
                color = FsTextSecondary.copy(alpha = 0.6f)
            )
        }
    }
}
