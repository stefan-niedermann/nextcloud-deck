package it.niedermann.nextcloud.deck.ui.util

import androidx.compose.ui.graphics.Color

/**
 * Converts java.awt.Color to androidx.compose.ui.graphics.Color
 */
fun java.awt.Color.toComposeColor(): Color {
    return Color(red, green, blue, alpha)
}

/**
 * Converts androidx.compose.ui.graphics.Color to java.awt.Color
 */
fun Color.toAwtColor(): java.awt.Color {
    return java.awt.Color(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        (alpha * 255).toInt()
    )
}
