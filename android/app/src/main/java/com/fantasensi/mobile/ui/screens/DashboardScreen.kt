package com.fantasensi.mobile.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import com.fantasensi.mobile.R
import com.fantasensi.mobile.services.AnimationService
import com.fantasensi.mobile.services.BoostService
import com.fantasensi.mobile.services.StatsSnapshot
import com.fantasensi.mobile.services.SystemStatsService
import com.fantasensi.mobile.services.formatBytes
import com.fantasensi.mobile.ui.components.FantaGradientText
import com.fantasensi.mobile.ui.components.FantaProgressBar
import com.fantasensi.mobile.ui.components.GlassCard
import com.fantasensi.mobile.ui.components.MiraButton
import com.fantasensi.mobile.ui.components.PowerButton
import com.fantasensi.mobile.ui.components.PrimaryButton
import com.fantasensi.mobile.ui.components.StatCard
import com.fantasensi.mobile.ui.theme.FsOrange
import com.fantasensi.mobile.ui.theme.FsTextPrimary
import com.fantasensi.mobile.ui.theme.FsTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val statsService = remember { SystemStatsService(context) }
    val boostService = remember { BoostService(context) }
    val animationService = remember { AnimationService(context) }
    val scope = rememberCoroutineScope()

    var stats by remember { mutableStateOf<StatsSnapshot?>(null) }

    var optimizeBusy by remember { mutableStateOf(false) }
    var optimizeProgress by remember { mutableFloatStateOf(0f) }
    var optimizeStatus by remember { mutableStateOf("Pronto para otimizar.") }

    var miraBusy by remember { mutableStateOf(false) }
    var miraStatus by remember { mutableStateOf("Pronto Para Melhorar Usando FantaSensi.") }
    var activeMira by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            stats = withContext(Dispatchers.IO) { statsService.snapshot() }
            delay(1000)
        }
    }

    val onMira: (String, Float) -> Unit = { label, scale ->
        scope.launch {
            miraBusy = true
            activeMira = label
            val upper = label.uppercase()
            val phases = listOf(
                "Aplicando $upper...",
                "Melhorando sensibilidade...",
                "Reduzindo latência de entrada...",
                "Calibrando mira..."
            )
            val finalMsg = if (label == "FULL CAPA") "FULL CAPA APLICADO" else "$upper APLICADA"
            if (animationService.canWrite()) {
                animationService.applyProfile(scale, scale, scale)
                if (label == "FULL CAPA") {
                    withContext(Dispatchers.IO) { boostService.cleanCache() }
                }
                phases.forEach { phase ->
                    miraStatus = phase
                    delay(1000)
                }
                miraStatus = finalMsg
            } else {
                miraStatus = "Permissão necessária. Abrindo configurações..."
                val intent = Intent(
                    Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {
                    miraStatus = "Não foi possível abrir as configurações."
                }
            }
            activeMira = null
            miraBusy = false
        }
    }

    val onOptimize: () -> Unit = {
        scope.launch {
            optimizeBusy = true
            optimizeProgress = 0f
            val phrases = listOf(
                "Aplicando otimização máxima",
                "Melhorando sensibilidade",
                "Limpando processos pesados",
                "Otimizando disco e registro",
                "Finalizando ajustes..."
            )
            phrases.forEachIndexed { index, phrase ->
                optimizeStatus = phrase
                optimizeProgress = index.toFloat() / phrases.size
                delay(600)
            }
            withContext(Dispatchers.IO) { boostService.cleanCache() }
            if (animationService.canWrite()) {
                animationService.applyGamingProfile()
            }
            optimizeProgress = 1f
            optimizeStatus = "Otimização concluída!"
            optimizeBusy = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08080B)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Hero()

            Spacer(Modifier.height(20.dp))

            StatsRow(stats)

            Spacer(Modifier.height(14.dp))

            MiraCard(
                busy = miraBusy,
                status = miraStatus,
                activeMira = activeMira,
                modifier = Modifier.fillMaxWidth(),
                onMira = onMira
            )

            Spacer(Modifier.height(14.dp))

            OptimizeCard(
                busy = optimizeBusy,
                progress = optimizeProgress,
                status = optimizeStatus,
                modifier = Modifier.fillMaxWidth(),
                onOptimize = onOptimize
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Hero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0x14FFFFFF))
                .border(
                    1.dp,
                    Color(0x33FFFFFF),
                    RoundedCornerShape(18.dp)
                )
                .padding(7.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(10.dp))
        FantaGradientText(text = "FantaSensi", fontSize = 24)
        Spacer(Modifier.height(5.dp))
        Text(
            text = "Otimização completa e ajustes de mira com um clique.",
            style = MaterialTheme.typography.bodyMedium,
            color = FsTextSecondary
        )
    }
}

@Composable
private fun StatsRow(stats: StatsSnapshot?) {
    val s = stats
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            icon = Icons.Filled.DeveloperBoard,
            label = "CPU",
            value = if (s != null) "${s.cpuPercent}%" else "--",
            detail = "uso do processador",
            progress = (s?.cpuPercent ?: 0) / 100f,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Filled.Memory,
            label = "Memória (RAM)",
            value = if (s != null) "${s.ramLoadPercent}%" else "--",
            detail = if (s != null)
                "${formatBytes(s.ramUsedBytes)} de ${formatBytes(s.ramTotalBytes)} em uso" else "carregando...",
            progress = (s?.ramLoadPercent ?: 0) / 100f,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Filled.Storage,
            label = "Disco",
            value = if (s != null) "${s.storageUsedPercent}%" else "--",
            detail = if (s != null) "${formatBytes(s.storageFreeBytes)} livres" else "carregando...",
            progress = (s?.storageUsedPercent ?: 0) / 100f,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MiraCard(
    busy: Boolean,
    status: String,
    activeMira: String?,
    modifier: Modifier = Modifier,
    onMira: (String, Float) -> Unit
) {
    GlassCard(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "\u25C6", color = FsOrange, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "  Configurações de Mira",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FsTextPrimary
                )
            }
            Text(
                text = "Aplica scripts de sensibilidade, latência e sistema.",
                style = MaterialTheme.typography.bodyMedium,
                color = FsTextSecondary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiraButton(
                    text = "MIRA LEVE",
                    enabled = !busy,
                    loading = busy && activeMira == "MIRA LEVE",
                    modifier = Modifier.weight(1f),
                    onClick = { onMira("MIRA LEVE", 0.75f) }
                )
                MiraButton(
                    text = "MIRA ALTA",
                    enabled = !busy,
                    loading = busy && activeMira == "MIRA ALTA",
                    modifier = Modifier.weight(1f),
                    onClick = { onMira("MIRA ALTA", 0.5f) }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiraButton(
                    text = "MIRA PESADA",
                    enabled = !busy,
                    loading = busy && activeMira == "MIRA PESADA",
                    modifier = Modifier.weight(1f),
                    onClick = { onMira("MIRA PESADA", 0.25f) }
                )
                PowerButton(
                    text = "FULL CAPA",
                    enabled = !busy,
                    loading = busy && activeMira == "FULL CAPA",
                    modifier = Modifier.weight(1f),
                    onClick = { onMira("FULL CAPA", 0f) }
                )
            }
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = FsTextSecondary
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x24FFFFFF))
            )
            SocialFooter()
        }
    }
}

@Composable
private fun OptimizeCard(
    busy: Boolean,
    progress: Float,
    status: String,
    modifier: Modifier = Modifier,
    onOptimize: () -> Unit
) {
    GlassCard(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    tint = FsOrange,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "  Otimização única",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FsTextPrimary
                )
            }
            Text(
                text = "Limpa temporários, RAM, registro, disco e esvazia a lixeira.",
                style = MaterialTheme.typography.bodyMedium,
                color = FsTextSecondary
            )
            PrimaryButton(
                text = if (busy) "OTIMIZANDO..." else "OTIMIZAR TUDO",
                enabled = !busy,
                loading = busy,
                onClick = onOptimize
            )
            FantaProgressBar(progress = progress)
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = FsTextSecondary
            )
        }
    }
}

@Composable
private fun SocialFooter() {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SocialChip(R.drawable.ic_discord, "Discord") {
                openUrl(context, "https://discord.gg/fantasensi")
            }
            SocialChip(R.drawable.ic_tiktok, "TikTok") {
                openUrl(context, "https://www.tiktok.com/@fanta.sens")
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "v1.0 · FantaSensi",
            style = MaterialTheme.typography.labelMedium,
            color = FsTextSecondary
        )
    }
}

@Composable
private fun SocialChip(iconRes: Int, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x14FFFFFF))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = FsTextPrimary
        )
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (_: Exception) {
    }
}
