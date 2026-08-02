package it.niedermann.nextcloud.deck.ui.util

import com.materialkolor.blend.Blend
import it.niedermann.nextcloud.deck.util.ColorHarmonizer
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AndroidColorHarmonizer @Inject constructor() : ColorHarmonizer {
    override fun harmonize(color: Int, keyColor: Int): Int {
        return Blend.harmonize(color, keyColor)
    }
}
