package com.aibuilt.blackvault.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MatrixGreen = Color(0xFF00FF66)
val MatrixGreenDim = Color(0xFF12A854)
val AlertRed = Color(0xFFFF3B30)
val AmberWarn = Color(0xFFFFB020)
val DimText = Color(0xFF6F8F78)
val VaultPanel = Color(0xFF050A06)
val VaultPanelAlt = Color(0xFF0A140D)
val VaultBorder = Color(0x7300FF66)

private val Mono = FontFamily.Monospace

val VaultTypography = Typography(
    displayLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 42.sp, letterSpacing = 2.sp),
    displayMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 34.sp, letterSpacing = 2.sp),
    displaySmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 1.sp),
    headlineLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 26.sp, letterSpacing = 1.sp),
    headlineMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 1.sp),
    headlineSmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 19.sp),
    titleLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 2.sp),
    titleMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 1.sp),
    titleSmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 1.sp),
    bodyLarge = TextStyle(fontFamily = Mono, fontSize = 15.sp, lineHeight = 21.sp),
    bodyMedium = TextStyle(fontFamily = Mono, fontSize = 13.sp, lineHeight = 19.sp),
    bodySmall = TextStyle(fontFamily = Mono, fontSize = 11.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.5.sp),
    labelMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 1.sp),
    labelSmall = TextStyle(fontFamily = Mono, fontSize = 10.sp, letterSpacing = 1.sp)
)

private val VaultColors = darkColorScheme(
    primary = MatrixGreen,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF06210F),
    onPrimaryContainer = MatrixGreen,
    secondary = MatrixGreenDim,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF08200F),
    onSecondaryContainer = MatrixGreen,
    tertiary = AmberWarn,
    onTertiary = Color.Black,
    background = Color.Black,
    onBackground = MatrixGreen,
    surface = Color.Black,
    onSurface = MatrixGreen,
    surfaceVariant = VaultPanel,
    onSurfaceVariant = DimText,
    error = AlertRed,
    onError = Color.Black,
    outline = MatrixGreenDim
)

@Composable
fun BlackVaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VaultColors,
        typography = VaultTypography,
        content = content
    )
}
