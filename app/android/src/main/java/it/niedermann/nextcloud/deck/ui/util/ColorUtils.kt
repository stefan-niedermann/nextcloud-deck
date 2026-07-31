package it.niedermann.nextcloud.deck.ui.util

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import it.niedermann.nextcloud.deck.util.ColorUtil

val LocalColorUtil = staticCompositionLocalOf<ColorUtil> {
    error("No ColorUtil provided")
}

/**
 * Converts it.niedermann.nextcloud.deck.domain.model.Color to androidx.compose.ui.graphics.Color
 */
fun it.niedermann.nextcloud.deck.domain.model.Color.toComposeColor(): Color {
    return Color(red, green, blue, alpha)
}

/**
 * Converts androidx.compose.ui.graphics.Color to it.niedermann.nextcloud.deck.domain.model.Color
 */
fun Color.toDomainColor(): it.niedermann.nextcloud.deck.domain.model.Color {
    return it.niedermann.nextcloud.deck.domain.model.Color(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        (alpha * 255).toInt()
    )
}
