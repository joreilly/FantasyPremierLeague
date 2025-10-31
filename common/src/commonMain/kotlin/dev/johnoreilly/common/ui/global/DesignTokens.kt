package dev.johnoreilly.common.ui.global

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized design tokens for consistent spacing, sizing, and dimensions throughout the app.
 * Following Material Design 3 spacing guidelines.
 */
object Spacing {
    val none: Dp = 0.dp
    val extraSmall: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 12.dp
    val mediumLarge: Dp = 16.dp
    val large: Dp = 20.dp
    val extraLarge: Dp = 24.dp
    val extraExtraLarge: Dp = 32.dp
    val huge: Dp = 48.dp
}

/**
 * Standard elevation values for surfaces and cards
 */
object Elevation {
    val none: Dp = 0.dp
    val small: Dp = 2.dp
    val medium: Dp = 4.dp
    val large: Dp = 8.dp
}

/**
 * Standard corner radius values
 */
object CornerRadius {
    val small: Dp = 4.dp
    val medium: Dp = 8.dp
    val large: Dp = 10.dp
    val extraLarge: Dp = 12.dp
    val round: Dp = 16.dp
}

/**
 * Standard icon sizes
 */
object IconSize {
    val small: Dp = 16.dp
    val medium: Dp = 24.dp
    val large: Dp = 32.dp
    val extraLarge: Dp = 48.dp
}

/**
 * Image and avatar sizes
 */
object ImageSize {
    val small: Dp = 40.dp
    val medium: Dp = 60.dp
    val large: Dp = 80.dp
    val extraLarge: Dp = 120.dp
}

/**
 * Button heights
 */
object ButtonHeight {
    val small: Dp = 32.dp
    val medium: Dp = 40.dp
    val large: Dp = 48.dp
}

/**
 * Divider thickness
 */
object DividerThickness {
    val thin: Dp = 0.5.dp
    val standard: Dp = 1.dp
    val thick: Dp = 2.dp
}
