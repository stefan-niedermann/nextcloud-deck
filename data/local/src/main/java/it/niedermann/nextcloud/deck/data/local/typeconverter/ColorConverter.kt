package it.niedermann.nextcloud.deck.data.local.typeconverter

import androidx.room3.ColumnTypeConverter
import it.niedermann.nextcloud.deck.domain.model.Color

class ColorConverter {
    @ColumnTypeConverter
    fun fromInt(value: Int?): Color? {
        return value?.let { Color(it) }
    }

    @ColumnTypeConverter
    fun toInt(color: Color?): Int? {
        return color?.argb
    }
}
