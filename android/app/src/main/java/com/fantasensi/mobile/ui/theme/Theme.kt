package com.fantasensi.mobile.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val FsDarkColors = darkColorScheme(
    primary = FsPrimary,
    onPrimary = Color.White,
    primaryContainer = FsPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = FsPrimaryLight,
    onSecondary = Color.White,
    background = FsBackground,
    onBackground = FsTextPrimary,
    surface = FsSurface,
    onSurface = FsTextPrimary,
    surfaceVariant = FsSurfaceLight,
    onSurfaceVariant = FsTextSecondary,
    error = FsDanger
)

@Composable
fun FantaSensiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FsDarkColors,
        typography = AppTypography
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = FsBackground,
            contentColor = FsTextPrimary
        ) {
            content()
        }
    }
}
