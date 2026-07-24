package it.niedermann.nextcloud.deck.ui.util

import androidx.compose.ui.graphics.Color

/**
 * Converts java.awt.Color to androidx.compose.ui.graphics.Color
 */
fun java.awt.Color.toComposeColor(): Color {
    return Color(red, green, blue, alpha)
}
