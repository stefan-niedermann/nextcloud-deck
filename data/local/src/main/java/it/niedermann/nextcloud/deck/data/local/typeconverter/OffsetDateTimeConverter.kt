package it.niedermann.nextcloud.deck.data.local.typeconverter

import androidx.room3.ColumnTypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OffsetDateTimeConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @ColumnTypeConverter
    fun fromString(value: String?): OffsetDateTime? {
        return value?.let { OffsetDateTime.parse(it, formatter) }
    }

    @ColumnTypeConverter
    fun toString(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }
}
