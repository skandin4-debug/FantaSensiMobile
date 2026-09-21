package com.fantasensi.mobile.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantasensi.mobile.ui.theme.FsGlass
import com.fantasensi.mobile.ui.theme.FsGlassBorder
import com.fantasensi.mobile.ui.theme.FsOrange
import com.fantasensi.mobile.ui.theme.FsOrangeDeep
import com.fantasensi.mobile.ui.theme.FsOrangeLight
import com.fantasensi.mobile.ui.theme.FsPrimary
import com.fantasensi.mobile.ui.theme.FsPrimaryDark
import com.fantasensi.mobile.ui.theme.FsPrimaryLight
import com.fantasensi.mobile.ui.theme.FsSurfaceLight
import com.fantasensi.mobile.ui.theme.FsTextPrimary
import com.fantasensi.mobile.ui.theme.FsTextSecondary

@Composable
fun FantaGradientText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 30
) {
    val transition = rememberInfiniteTransition(label = "title")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shift"
    )
    val period = 500f
    val brush = Brush.horizontalGradient(
        colors = listOf(FsOrangeLight, FsOrange, FsOrangeDeep, FsOrange, FsOrangeLight),
        startX = shift * period,
        endX = shift * period + period,
        tileMode = TileMode.Repeated
    )
    val glow = Shadow(
        color = FsOrange.copy(alpha = 0.7f),
        offset = Offset.Zero,
        blurRadius = 18f
    )
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            brush = brush,
            shadow = glow
        ),
        color = Color.Unspecified,
        modifier = modifier
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    padding: Dp = 18.dp,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(FsGlass)
            .border(
                width = 1.dp,
                color = FsGlassBorder,
                shape = RoundedCornerShape(cornerRadius)
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(Modifier.padding(padding)) { content() }
    }
}

@Composable
fun FantaProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 9.dp
) {
    val radius = height / 2
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(radius))
            .background(Color(0x22FFFFFF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(radius))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFF9E42), FsPrimaryLight, FsPrimary)
                    )
                )
        )
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    detail: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier, padding = 10.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FsPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = FsTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                fontWeight = FontWeight.Bold,
                color = FsOrangeLight
            )
            FantaProgressBar(progress = progress, height = 6.dp)
            Text(
                text = detail,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 9.sp),
                color = FsTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FsPrimary,
            contentColor = Color.White,
            disabledContainerColor = FsPrimary.copy(alpha = 0.45f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.size(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun MiraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    ActionButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        contentColor = FsTextPrimary,
        background = Brush.linearGradient(
            listOf(Color(0x1EFF7A00), Color(0x10000000))
        ),
        borderColor = Color(0x59FF8A1E)
    )
}

@Composable
fun PowerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    ActionButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        contentColor = Color.White,
        background = Brush.linearGradient(
            listOf(FsOrange, FsOrangeDeep, Color(0xFF9A4402))
        ),
        borderColor = Color(0xFFFFA43A)
    )
}

@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    loading: Boolean,
    contentColor: Color,
    background: Brush,
    borderColor: Color
) {
    val dim = if (loading) 0.85f else if (enabled) 1f else 0.55f
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .alpha(dim)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 13.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = contentColor,
                    strokeWidth = 2.dp,
                    trackColor = contentColor.copy(alpha = 0.3f)
                )
                Spacer(Modifier.size(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
