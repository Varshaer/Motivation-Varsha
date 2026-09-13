package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = GoldPrimary,
  onPrimary = Color(0xFF1C1300),
  primaryContainer = GoldContainer,
  onPrimaryContainer = OnGoldContainer,
  secondary = GoldAccent,
  onSecondary = Color(0xFF1F1500),
  tertiary = SkyBlue,
  onTertiary = Color(0xFF002030),
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = DarkBorder,
  outlineVariant = DarkBorderLight
)

private val LightColorScheme = lightColorScheme(
  primary = GoldDark,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFFEF3C7),
  onPrimaryContainer = Color(0xFF78350F),
  secondary = Color(0xFFD97706),
  onSecondary = Color.White,
  tertiary = SkyBlue,
  onTertiary = Color.White,
  background = Color(0xFFF9FAFB),
  onBackground = Color(0xFF111827),
  surface = Color.White,
  onSurface = Color(0xFF111827),
  surfaceVariant = Color(0xFFF3F4F6),
  onSurfaceVariant = Color(0xFF4B5563),
  outline = Color(0xFFE5E7EB),
  outlineVariant = Color(0xFFD1D5DB)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to Motivator's signature dark luxury look
  dynamicColor: Boolean = false, // Keep signature gold brand identity
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.surface.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
